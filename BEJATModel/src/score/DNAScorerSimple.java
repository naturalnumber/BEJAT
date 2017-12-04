package score;

public class DNAScorerSimple extends DNAScorer {
    private final int match;
    private final int mismatch;
    private final int gapOpen;
    private final int gapExtend;

    public DNAScorerSimple(int match, int mismatch, int gap) {
        this(match, mismatch, gap, gap);
    }

    public DNAScorerSimple(int match, int mismatch, int gapOpen, int gapExtend) {
        this.match = match;
        this.mismatch = mismatch;
        this.gapOpen = gapOpen;
        this.gapExtend = gapExtend;
    }

    @Override
    public int s(char a, char b) {
        return (a == b) ? match : mismatch;
    }

    @Override
    public int s(int a, int b) {
        return (a == b) ? match : mismatch;
    }

    @Override
    public int w(int l) {
        return (l == 0) ? gapOpen : gapExtend;
    }

    @Override
    public boolean isSimpleGap() {
        return gapOpen == gapExtend;
    }

    @Override
    public boolean isSimpleExtension() {
        return true;
    }

    //  Supposed to override
    @Override
    public int[][] initializeGlobal(int[][] globalScores) {
        int min = Math.min(globalScores.length, globalScores[0].length);
        int max = Math.max(globalScores.length, globalScores[0].length);
        boolean yLonger = globalScores.length > globalScores[0].length;

        int i = 2;
        int w = globalScores[1][0] = globalScores[0][1] = gapOpen;
        for (; i < min; i++) globalScores[i][0] = globalScores[0][i] = w += gapExtend; // gapOpen + gapExtend*(i-1);
        if (yLonger) for (; i < max; i++) globalScores[i][0] = w += gapExtend; // gapOpen + gapExtend*(i-1);
        else for (; i < max; i++) globalScores[0][i] = w += gapExtend; // gapOpen + gapExtend*(i-1);

        return globalScores;
    }
}
