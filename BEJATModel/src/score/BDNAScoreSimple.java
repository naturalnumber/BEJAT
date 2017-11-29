package score;

public class BDNAScoreSimple extends BDNAScore {
    private final int equal;
    private final int unequal;
    private final int gapOpen;
    private final int gapExtend;

    public BDNAScoreSimple(int equal, int unequal, int gap) {
        this(equal, unequal, gap, gap);
    }

    public BDNAScoreSimple(int equal, int unequal, int gapOpen, int gapExtend) {
        this.equal = equal;
        this.unequal = unequal;
        this.gapOpen = gapOpen;
        this.gapExtend = gapExtend;
    }

    @Override
    public int s(char a, char b) {
        return (a == b) ? equal : unequal;
    }

    @Override
    public int s(byte a, byte b) {
        return (a == b) ? equal : unequal;
    }

    @Override
    public int w(int l) {
        return (l == 0) ? gapOpen : gapExtend;
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
