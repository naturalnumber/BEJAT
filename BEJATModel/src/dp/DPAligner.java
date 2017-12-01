package dp;

import java.util.ArrayList;

import alignment.Alignment;
import score.Scorer;
import sequence.Seq;

/**
 * https://en.wikipedia.org/wiki/Sequence_alignment
 * <p>
 * https://en.wikipedia.org/wiki/Dynamic_programming https://www.topcoder.com/community/data-science/data-science-tutorials/dynamic-programming-from-novice-to-advanced/
 * https://www.codeproject.com/Articles/304772/DNA-Sequence-Alignment-using-Dynamic-Programming-A
 * <p>
 * https://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm https://en.wikipedia.org/wiki/Smith%E2%80%93Waterman_algorithm
 * <p>
 * https://en.wikipedia.org/wiki/Substitution_matrix
 */

public class DPAligner implements Runnable {
    private final static int WORD_SIZE = 64;
    private final static int N_PER     = 21;

    public final static long GLOBAL_FLAG = 1;
    public final static long LOCAL_FLAG  = 2;

    protected final static byte GPN_F = 1; // North
    protected final static byte MMM_F = 2; // Diagonal
    protected final static byte GPW_F = 4; // West

    protected final static byte MSK = 7; // All

    protected final static long[] MASKS = getMasks();
    protected final static long[] GPN   = getFlags(GPN_F);
    protected final static long[] MMM   = getFlags(MMM_F);
    protected final static long[] GPW   = getFlags(GPW_F);

    protected final Seq      first;
    protected final Seq      second;
    protected final Scorer   scorer;
    protected final int[][]  globalScores;
    protected final int[][]  localScores;
    protected final long[][] gAdjacency;
    protected final long[][] lAdjacency;

    protected final ArrayList<Alignment> gAlignments;
    protected final ArrayList<Alignment> lAlignments;

    protected final    int    F; // First
    protected final    int    S; // Second
    protected final    int    W; // First
    protected final    int    H; // Second
    protected final    int    A;
    protected final    double total;
    protected volatile int    done;
    protected final    int    AdjW; // First
    protected final    int    AdjH; // Second

    protected final    boolean doGlobal;
    protected final    boolean doLocal;
    protected volatile boolean complete;
    protected volatile boolean alligned;
    protected volatile boolean errored;

    public DPAligner(Seq first, Seq second, Scorer scorer, int flag) {
        this(first, second, scorer, (flag & GLOBAL_FLAG) == GLOBAL_FLAG,
             (flag & LOCAL_FLAG) == LOCAL_FLAG);
    }

    public DPAligner(Seq first, Seq second, Scorer scorer, boolean doGlobal, boolean doLocal) {
        if (first == null) {
            throw new IllegalArgumentException("Invalid input: first " + first);
        }
        if (second == null) {
            throw new IllegalArgumentException("Invalid input: second " + first);
        }
        if (scorer == null) {
            throw new IllegalArgumentException("Invalid input: scorer " + scorer);
        }
        if (!first.sameType(second)) {
            throw new IllegalArgumentException("Type mismatch: " + first.getType() + " and " +
                                               second.getType());
        }
        if (scorer.sameType(first)) {
            throw new IllegalArgumentException("Scorer type mismatch: " + first.getType() +
                                               " and " +
                                               scorer.getType());
        }

        this.first = first;
        this.second = second;
        this.scorer = scorer;

        this.F = first.length();
        this.S = second.length();
        this.W = first.length() + 1;
        this.H = second.length() + 1;
        this.A = H * W;
        this.AdjW = adjWidthFromSize(F);
        this.AdjH = S;

        this.doGlobal = doGlobal;
        this.doLocal = doLocal;

        this.total = ((this.doGlobal || this.doLocal) ? A : 0) + 1;

        if (doGlobal) {
            this.globalScores = new int[H][W];
            this.gAdjacency = new long[AdjH][AdjW];

            //  Initialize
            //  Needleman–Wunsch Algorithm
            //for (int[] ints : this.globalScores) Arrays.fill(ints, 0); // Should be unnecessary
            scorer.initializeGlobal(this.globalScores);

            this.gAlignments = new ArrayList<>();
        } else {
            this.globalScores = null;
            this.gAdjacency = null;
            this.gAlignments = null;
        }

        if (doLocal) {
            this.localScores = new int[H][W];
            this.lAdjacency = new long[AdjH][AdjW];

            //  Initialize
            //  Smith–Waterman Algorithm
            //for (int[] ints : this.localScores) Arrays.fill(ints, 0); // Should be unnecessary

            this.lAlignments = new ArrayList<>();
        } else {
            this.localScores = null;
            this.lAdjacency = null;
            this.lAlignments = null;
        }

        this.done = ((this.doGlobal) ? F + S + 1 : 0);

        this.complete = !doRun();
        this.alligned = this.complete;
        this.errored = false;

        if (this.alligned) this.done++;
    }

    @Override
    public void run() {
        while (!errored && doRun() && !complete && !alligned) {
            if (!complete) {
                if (scorer.isSimpleGap()) {
                    if (doGlobal && doLocal) {
                        errored = !doAllSimple();
                    } else if (doGlobal) {
                        errored = !doGlobalSimple();
                    } else if (doLocal) {
                        errored = !doLocalSimple();
                    }
                } else {
                    errored = true;
                    throw new IllegalArgumentException("Complex gaps are experimental!");
                }
                this.complete = true;
            } else if (!alligned) {
                if (doGlobal) {
                    errored = !alignGlobal();
                }
                if (doLocal) {
                    errored = !alignLocal();
                }
                this.done++;
                this.alligned = true;
            }// else {}
        }
        return;
    }

    public boolean doAllSimple() {
        if (scorer == null || first == null || second == null) return false;
        if (this.globalScores == null || gAdjacency == null) return false;
        if (this.localScores == null || lAdjacency == null) return false;

        //  Constants
        int w = scorer.w(0);

        // Temp variables, Global
        int   secondValue;
        int   match, gMax, lMax;
        int   gGPN, gScoreN, lGPN, lScoreN; // North
        int   gMMM, gScoreD, lMMM, lScoreD; // Diagonal
        int   gGPW, gScoreW, lGPW, lScoreW; // West
        int[] gScores, lScores, gScoresN, lScoresN;
        int[] firstValues = first.values(1);

        //  scores[j][i] is H x W = A in size
        //  0 row/column is done in initialization

        //  Initialize run
        gScoresN = this.globalScores[0];
        lScoresN = this.localScores[0];

        for (int j = 1; j < H; j++) { // Second sequence
            secondValue = second.charAt(j - 1);

            //  Initialize row
            gScores = this.globalScores[j];
            lScores = this.localScores[j];
            gScoreW = gScores[0];
            lScoreW = lScores[0];
            gScoreD = gScoresN[0];
            lScoreD = lScoresN[0];

            for (int i = 1; i < W; i++) { // First sequence
                //  Used multiple places
                gScoreN = gScoresN[i];
                lScoreN = lScoresN[i];

                //  Match/Mismatch score
                match = scorer.s(firstValues[i], secondValue);

                //  Global scores
                gGPN = gScoreN + w; // North
                gMMM = gScoreD + match; // North West
                gGPW = gScoreW + w; // West

                //  Local scores
                lGPN = lScoreN + w; // North
                lMMM = lScoreD + match; // North West
                lGPW = lScoreW + w; // West

                //  Score for current place
                gScores[i] = gMax = max(gGPN, gMMM, gGPW);
                lScores[i] = lMax = max(lGPN, lMMM, lGPW, 0);

                //  Check vertical gap
                if (gMax == gGPN) addAdj(this.gAdjacency, j, i, GPN);

                //  Check match/mismatch alignment
                if (gMax == gMMM) addAdj(this.gAdjacency, j, i, MMM);

                //  Check horizontal gap
                if (gMax == gGPW) addAdj(this.gAdjacency, j, i, GPW);

                //  Check vertical gap
                if (lMax == lGPN) addAdj(this.lAdjacency, j, i, GPN);

                //  Check match/mismatch alignment
                if (lMax == lMMM) addAdj(this.lAdjacency, j, i, MMM);

                //  Check horizontal gap
                if (lMax == lGPW) addAdj(this.lAdjacency, j, i, GPW);

                //  Prep for next
                gScoreW = gMax;
                lScoreW = lMax;
                gScoreD = gScoreN;
                lScoreD = lScoreN;
            }

            //  Shift values
            gScoresN = gScores;
            lScoresN = lScores;
            this.done += F;
        }

        return true;
    }

    public boolean doGlobalSimple() {
        if (scorer == null || first == null || second == null) return false;
        if (this.globalScores == null || gAdjacency == null) return false;

        //  Constants
        int w = scorer.w(0);

        // Temp variables, Global
        int   secondValue;
        int   gMax;
        int   gGPN, gScoreN; // North
        int   gMMM, gScoreD; // Diagonal
        int   gGPW, gScoreW; // West
        int[] gScores, gScoresN;
        int[] firstValues = first.values(1);

        //  scores[j][i] is H x W = A in size
        //  0 row/column is done in initialization

        //  Initialize run
        gScoresN = this.globalScores[0];

        for (int j = 1; j < H; j++) { // Second sequence
            secondValue = second.charAt(j - 1);

            //  Initialize row
            gScores = this.globalScores[j];
            gScoreW = gScores[0];
            gScoreD = gScoresN[0];

            for (int i = 1; i < W; i++) { // First sequence
                //  Used multiple places
                gScoreN = gScoresN[i];

                //  Global scores
                gGPN = gScoreN + w; // North
                gMMM = gScoreD + scorer.s(firstValues[i], secondValue); // North West
                gGPW = gScoreW + w; // West

                //  Score for current place
                gScores[i] = gMax = max(gGPN, gMMM, gGPW);

                //  Check vertical gap
                if (gMax == gGPN) addAdj(this.gAdjacency, j, i, GPN);

                //  Check match/mismatch alignment
                if (gMax == gMMM) addAdj(this.gAdjacency, j, i, MMM);

                //  Check horizontal gap
                if (gMax == gGPW) addAdj(this.gAdjacency, j, i, GPW);

                //  Prep for next
                gScoreW = gMax;
                gScoreD = gScoreN;
            }

            //  Shift values
            gScoresN = gScores;
            this.done += F;
        }

        return true;
    }

    public boolean doLocalSimple() {
        if (scorer == null || first == null || second == null) return false;
        if (this.localScores == null || lAdjacency == null) return false;

        //  Constants
        int w = scorer.w(0);

        // Temp variables, Global
        int   secondValue;
        int   match, lMax;
        int   lGPN, lScoreN; // North
        int   lMMM, lScoreD; // Diagonal
        int   lGPW, lScoreW; // West
        int[] lScores, lScoresN;
        int[] firstValues = first.values(1);

        //  scores[j][i] is H x W = A in size
        //  0 row/column is done in initialization

        //  Initialize run
        lScoresN = this.localScores[0];

        for (int j = 1; j < H; j++) { // Second sequence
            secondValue = second.charAt(j - 1);

            //  Initialize row
            lScores = this.localScores[j];
            lScoreW = lScores[0];
            lScoreD = lScoresN[0];

            for (int i = 1; i < W; i++) { // First sequence
                //  Used multiple places
                lScoreN = lScoresN[i];

                //  Local scores
                lGPN = lScoreN + w; // North
                lMMM = lScoreD + scorer.s(firstValues[i], secondValue); // North West
                lGPW = lScoreW + w; // West

                //  Score for current place
                lScores[i] = lMax = max(lGPN, lMMM, lGPW, 0);

                //  Check vertical gap
                if (lMax == lGPN) addAdj(this.lAdjacency, j, i, GPN);

                //  Check match/mismatch alignment
                if (lMax == lMMM) addAdj(this.lAdjacency, j, i, MMM);

                //  Check horizontal gap
                if (lMax == lGPW) addAdj(this.lAdjacency, j, i, GPW);

                //  Prep for next
                lScoreW = lMax;
                lScoreD = lScoreN;
            }

            //  Shift values
            lScoresN = lScores;
            this.done += F;
        }

        return true;
    }

    private boolean alignGlobal() {

    }

    private boolean alignLocal() {

    }

    private static void addAdj(long[][] gAdjacency, int j, int i, long[] flags) {
        gAdjacency[j][adjW(i)] |= flags[adjR(i)];
    }

    private int[] initializeGapLength(int x) {
        int[] gl = new int[x];

        //Arrays.fill(gl, 0); // Should be unnecessary

        for (int i = 1; i < x; i++) gl[i] = i - 1;

        return gl;
    }

    //  Synchronized?
    public double getProgress() {
        return this.done / this.total;
    }

    //  Helper methods
    protected boolean doRun() {
        return this.doGlobal || this.doLocal;
    }

    //  Static helper methods
    protected static long[] getMasks() {
        long[] masks = new long[N_PER]; // N_PER = 21 = (64/3)

        masks[0] = 7L;
        for (int i = 1; i < N_PER; i++) masks[i] = 7L << (3 * i);

        return masks;
    }

    protected static long[] getFlags(long flag) {
        long[] flags = new long[N_PER]; // N_PER = 21 = (64/3)

        flags[0] = flag;
        for (int i = 1; i < N_PER; i++) flags[i] = flag << (3 * i);

        return flags;
    }

    private static int adjWidthFromSize(int size) {
        return size / N_PER + ((size % N_PER > 0) ? 1 : 0); //  N_PER = 21 = (64/3)
    }

    private static int min(int a, int b, int c) {
        return (a <= b) ? ((a <= c) ? a : c) : ((b <= c) ? b : c);
    }

    private static int max(int a, int b, int c) {
        return (a >= b) ? ((a >= c) ? a : c) : ((b >= c) ? b : c);
    }

    private static int max(int a, int b, int c, int d) {
        int m1 = (a >= b) ? a : b;
        int m2 = (c >= d) ? c : d;
        return (m1 >= m2) ? m1 : m2;
    }

    private static int adjW(int i) {
        return i / N_PER; //  N_PER = 21 = (64/3)
    }

    private static int adjR(int i) {
        return i % N_PER; //  N_PER = 21 = (64/3)
    }

    //  Experimental


    /**
     * This is experimental. It likely works for gap schemes of decreasing weight, but should
     * transition to tracking all the various possible gap combinations. (If N is reachable by N and
     * D, then the gap length is either gaplength(N)+1 or 1 (start new)
     *
     * @return success
     */
    private boolean doAllComplex() {
        //  Constants

        // Temp variables, Global
        int   secondValue;
        int   match, gMax, lMax;
        int   gGPN, gScoreN, lGPN, lScoreN; // North
        int   gMMM, gScoreD, lMMM, lScoreD; // Diagonal
        int   gGPW, gScoreW, lGPW, lScoreW; // West
        int   gGLW, lGLW;
        int[] gScores, lScores, gScoresN, lScoresN;
        int[] gGLs, lGLs, gGLsN, lGLsN;
        int[] firstValues = first.values(1);

        //  scores[j][i] is H x W = A in size
        //  0 row/column is done in initialization

        //  Initialize run
        gScoresN = this.globalScores[0];
        lScoresN = this.localScores[0];
        gGLsN = initializeGapLength(W);
        lGLsN = new int[W];

        for (int j = 1; j < H; j++) { // Second sequence
            secondValue = second.charAt(j - 1);

            //  Initialize row
            gScores = this.globalScores[j];
            lScores = this.localScores[j];
            gScoreW = gScores[0];
            lScoreW = lScores[0];
            gScoreD = gScoresN[0];
            lScoreD = lScoresN[0];
            gGLs = new int[W]; // Check this
            lGLs = new int[W]; // Check this
            gGLW = 0;
            lGLW = 0;

            for (int i = 1; i < W; i++) { // First sequence
                //  Used multiple places
                gScoreN = gScoresN[i];
                lScoreN = lScoresN[i];

                //  Match/Mismatch score
                match = scorer.s(firstValues[i], secondValue);

                //  Global scores
                gGPN = gScoreN + scorer.w(gGLsN[i]); // North
                gMMM = gScoreD + match; // North West
                gGPW = gScoreW + scorer.w(gGLW); // West

                //  Local scores
                lGPN = lScoreN + scorer.w(lGLsN[i]); // North
                lMMM = lScoreD + match; // North West
                lGPW = lScoreW + scorer.w(lGLW); // West

                //  Score for current place
                gScores[i] = gMax = max(gGPN, gMMM, gGPW);
                lScores[i] = lMax = max(lGPN, lMMM, lGPW, 0);

                //  Check vertical gap
                if (gMax == gGPN) {
                    addAdj(this.gAdjacency, j, i, GPN);
                    gGLs[i] = gGLsN[i] + 1; // Check this
                }

                //  Check match/mismatch alignment
                if (gMax == gMMM) {
                    addAdj(this.gAdjacency, j, i, MMM);
                }

                //  Check horizontal gap
                if (gMax == gGPW) {
                    addAdj(this.gAdjacency, j, i, GPW);
                    gGLW = gGLW + 1; // Next // Check this
                } else {
                    gGLW = 0; // Break gap
                }

                //  Check vertical gap
                if (lMax == lGPN) {
                    addAdj(this.lAdjacency, j, i, GPN);
                    lGLs[i] = lGLsN[i] + 1; // Check this
                }

                //  Check match/mismatch alignment
                if (lMax == lMMM) {
                    addAdj(this.lAdjacency, j, i, MMM);
                }

                //  Check horizontal gap
                if (lMax == lGPW) {
                    addAdj(this.lAdjacency, j, i, GPW);
                    lGLW = lGLW + 1; // Next // Check this
                } else {
                    lGLW = 0; // Break gap
                }

                //  Prep for next
                gScoreW = gMax;
                lScoreW = lMax;
                gScoreD = gScoreN;
                lScoreD = lScoreN;
            }

            //  Shift values
            gScoresN = gScores;
            lScoresN = lScores;
            gGLsN = gGLs;
            lGLsN = lGLs;
            this.done += F;
        }

        return true;
    }
}
