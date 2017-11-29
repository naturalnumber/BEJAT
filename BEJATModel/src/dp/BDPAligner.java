package dp;

import java.util.Arrays;

import score.BScore;
import sequence.BSeq;

/**
 * https://en.wikipedia.org/wiki/Sequence_alignment
 * https://en.wikipedia.org/wiki/Dynamic_programming
 * https://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm
 * https://en.wikipedia.org/wiki/Smith%E2%80%93Waterman_algorithm
 */

public class BDPAligner implements Runnable {
    public final static int GLOBAL_FLAG = 1;
    public final static int LOCAL_FLAG = 2;

    protected final static byte GP1 = 1; // North
    protected final static byte MMM = 2; // Diagonal
    protected final static byte GP2 = 4; // West

    protected final static byte MSK = 7; // All

    protected final static long[] MASKS = getMasks();

    protected final BSeq    first;
    protected final BSeq    second;
    protected final BScore  scorer;
    protected final int[][] scoresG;
    protected final int[][] scoresL;
    protected final long[][] adjacencyG;
    protected final long[][] adjacencyL;

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

    public BDPAligner(BSeq first, BSeq second, BScore scorer, int flag) {
        this(first, second, scorer, (flag & GLOBAL_FLAG) == GLOBAL_FLAG, (flag & LOCAL_FLAG) == LOCAL_FLAG);
    }
    public BDPAligner(BSeq first, BSeq second, BScore scorer, boolean doGlobal, boolean doLocal) {
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
            throw new IllegalArgumentException("Score type mismatch: " + first.getType() + " and " +
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
            this.scoresG = new int[H][W];
            this.adjacencyG = new long[AdjH][AdjW];

            //  Initialize
            //  Needleman–Wunsch Algorithm
            Arrays.fill(this.scoresG, 0);
            scorer.initializeGlobal(this.scoresG);
        } else {
            this.scoresG = null;
            this.adjacencyG = null;
        }

        if (doLocal) {
            this.scoresL = new int[H][W];
            this.adjacencyL = new long[AdjH][AdjW];

            //  Initialize
            //  Smith–Waterman Algorithm
            Arrays.fill(this.scoresL, 0);
        } else {
            this.scoresL = null;
            this.adjacencyL = null;
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
                // Temp variables
                int score, scoreG1, scoreD, s;

                //  scores[j][i] is H x W = A in size
                //  0 row/column is done in initialization
                for (int j = 1; j < H; j++) { // Second sequence

                    for (int i = 1; i < W; i++) { // First sequence
                        score
                        if (doGlobal) this.scoresG;
                    }
                    this.done += F;
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
        return i / 21; //  21 = (64/3)
    }
    private int adjR(int i) {
        return i % 21; //  21 = (64/3)
    }

    //  Static helper methods
    protected static long[] getMasks() {
        long[] masks = new long[64/3];

        for (int i = 0; i < masks.length; i++) masks[i] = (long) 7 << (3*i);

        return masks;
    }
    private static int adjWidthFromSize(int size) {
        return size / 21 + ((size % 21 > 0) ? 1 : 0); //  21 = (64/3)
    }
}
