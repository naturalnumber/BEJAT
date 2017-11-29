package dp;

import java.util.Arrays;

import score.Scorer;
import sequence.Seq;

/**
 * https://en.wikipedia.org/wiki/Sequence_alignment
 * https://en.wikipedia.org/wiki/Dynamic_programming
 * https://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm
 * https://en.wikipedia.org/wiki/Smith%E2%80%93Waterman_algorithm
 */

public class DPAligner implements Runnable {
    private final static int WORD_SIZE = 62;
    private final static int N_PER = 21;

    public final static int GLOBAL_FLAG = 1;
    public final static int LOCAL_FLAG = 2;

    protected final static byte GP1 = 1; // North
    protected final static byte MMM = 2; // Diagonal
    protected final static byte GP2 = 4; // West

    protected final static byte MSK = 7; // All

    protected final static long[] MASKS = getMasks();

    protected final Seq      first;
    protected final Seq      second;
    protected final Scorer   scorer;
    protected final int[][]  gScores;
    protected final int[][]  lScores;
    protected final long[][] gAdjacency;
    protected final long[][] lAdjacency;

    protected final int F; // First
    protected final int S; // Second
    protected final int W; // First
    protected final int H; // Second
    protected final int A;
    protected final double total;
    protected volatile int done;
    protected final int AdjW; // First
    protected final int AdjH; // Second

    protected final boolean doGlobal;
    protected final boolean doLocal;
    protected volatile boolean complete;
    protected volatile boolean alligned;

    public DPAligner(Seq first, Seq second, Scorer scorer, int flag) {
        this(first, second, scorer, (flag & GLOBAL_FLAG) == GLOBAL_FLAG, (flag & LOCAL_FLAG) == LOCAL_FLAG);
    }
    public DPAligner(Seq first, Seq second, Scorer scorer, boolean doGlobal, boolean doLocal) {
        if (first == null) {
            throw new IllegalArgumentException("Invalid input: first "+first);
        }
        if (second == null) {
            throw new IllegalArgumentException("Invalid input: second "+first);
        }
        if (scorer == null) {
            throw new IllegalArgumentException("Invalid input: scorer " + scorer);
        }
        if (!first.sameType(second)) {
            throw new IllegalArgumentException("Type mismatch: "+first.getType()+" and "+second.getType());
        }
        if (scorer.sameType(first)) {
            throw new IllegalArgumentException("Scorer type mismatch: " + first.getType() + " and " +
                                               scorer.getType());
        }

        this.first = first;
        this.second = second;
        this.scorer = scorer;

        this.F = first.length();
        this.S = second.length();
        this.W = first.length() + 1;
        this.H = second.length() + 1;
        this.A = H*W;
        this.AdjW = adjWidthFromSize(F);
        this.AdjH = S;

        this.doGlobal = doGlobal;
        this.doLocal = doLocal;

        this.total = ((this.doGlobal || this.doLocal) ? A : 0) + 1;

        if (doGlobal) {
            this.gScores = new int[H][W];
            this.gAdjacency = new long[AdjH][AdjW];

            //  Initialize
            //  Needleman–Wunsch Algorithm
            Arrays.fill(this.gScores, 0);
            scorer.initializeGlobal(this.gScores);
        } else {
            this.gScores = null;
            this.gAdjacency = null;
        }

        if (doLocal) {
            this.lScores = new int[H][W];
            this.lAdjacency = new long[AdjH][AdjW];

            //  Initialize
            //  Smith–Waterman Algorithm
            Arrays.fill(this.lScores, 0);
        } else {
            this.lScores = null;
            this.lAdjacency = null;
        }

        this.done = ((this.doGlobal) ? F+S+1 : 0);

        this.complete = !doRun();
        this.alligned = this.complete;

        if (this.alligned) this.done++;
    }

    @Override
    public void run() {
        boolean skip = false;
        while (!skip && doRun() && !complete && !alligned) {
            if (!complete) {
                if (scorer.isSimpleGap()) {
                    if (doGlobal && doLocal) {
                        //  Constants
                        int w = scorer.w(0);

                        // Temp variables, Global
                        char  f, s;
                        int   match, gap1, gap2, gScore, lScore;
                        int   gScoreG1, gScoreN, lScoreG1, lScoreN; // North
                        int   gScoreD, gScoreNW, lScoreD, lScoreNW; // Diagonal
                        int   gScoreG2, gScoreW, lScoreG2, lScoreW; // West
                        int[] gScoresN, lScoresN;

                        //  scores[j][i] is H x W = A in size
                        //  0 row/column is done in initialization

                        //  Initialize run


                        for (int j = 1; j < H; j++) { // Second sequence
                            s = second.charAt(j - 1);

                            //  Initialize row
                            gScoresN = this.gScores[j - 1];
                            lScoresN = this.lScores[j - 1];
                            gScoreW = this.gScores[j][0];
                            lScoreW = this.lScores[j][0];
                            gScoreNW = gScoresN[0];
                            lScoreNW = lScoresN[0];

                            for (int i = 1; i < W; i++) { // First sequence
                                f = first.charAt(i - 1);

                                gScoreN = this.gScores[j - 1][i];
                                lScoreN = this.lScores[j - 1][i];

                                gap1 = w;
                                match = scorer.s(f, s);
                                gap2 = w;

                                //  Todo: here

                                gScoreG1 = gScoreN + gap1;
                                gScoreD = gScoreN + gap1;
                                gScoreG2 = gScoreN + gap2;

                                gScore = min(gScoreG1, gScoreD, gScoreG2);
                                this.gScores[j][i] = gScore;
                                lScore = min(lScoreG1, lScoreD, lScoreG2);
                                this.lScores[j][i] = lScore;

                                //  Prep for next
                                gScoreW = gScore;
                                lScoreW = lScore;
                                gScoreNW = gScoreN;
                                lScoreNW = lScoreN;
                            }
                            this.done += F;
                        }
                    } else if (doGlobal) {

                    } else if (doLocal) {

                    }
                }
                this.complete = true;
            } else if (!alligned) {

                //  TODO:

                this.done++;
                this.alligned = true;
            } else {
            }
        }
        return;
    }

    //  Synchronized?
    public double getProgress() {
        return this.done / this.total;
    }

    //  Helper methods
    protected boolean doRun() {
        return this.doGlobal || this.doLocal;
    }
    private int adjW(int i) {
        return i / N_PER; //  N_PER = 21 = (64/3)
    }
    private int adjR(int i) {
        return i % N_PER; //  N_PER = 21 = (64/3)
    }

    //  Static helper methods
    protected static long[] getMasks() {
        long[] masks = new long[N_PER]; // N_PER = 21 = (64/3)

        for (int i = 0; i < masks.length; i++) masks[i] = (long) 7 << (3*i);

        return masks;
    }
    private static int adjWidthFromSize(int size) {
        return size / N_PER + ((size % N_PER > 0) ? 1 : 0); //  N_PER = 21 = (64/3)
    }
    private static int min(int a, int b, int c) {
        return (a <= b) ? ((a <= c) ? a : c) : ((b <= c) ? b : c);
    }
}
