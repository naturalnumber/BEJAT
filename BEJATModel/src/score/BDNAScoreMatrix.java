package score;

import sequence.BDNASeq;
import sequence.BNSeq;

public class BDNAScoreMatrix extends BDNAScore {
    private final int[][] scores;
    private final int gapOpen;
    private final int gapExtend;

    public BDNAScoreMatrix(int[][] scores, int gap) {
        this(scores, gap, gap);
    }

    public BDNAScoreMatrix(int[][] scores, int gapOpen, int gapExtend) {
        if ( scores == null || scores.length != N || scores[0].length != N ) {
            throw new IllegalArgumentException("Bad scoresG: "+scores);
        }
        this.scores = scores;
        this.gapOpen = gapOpen;
        this.gapExtend = gapExtend;
    }

    @Override
    public int s(char a, char b) {
        return scores[BDNASeq.interpret(a)][BDNASeq.interpret(b)];
    }

    @Override
    public int s(byte a, byte b) {
        return scores[a][b];
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
