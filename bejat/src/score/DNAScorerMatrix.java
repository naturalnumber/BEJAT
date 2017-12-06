package score;

import sequence.DNASeq;

public class DNAScorerMatrix extends DNAScorer implements MatrixScorer {
    private final int[][] scores;
    private final int gapOpen;
    private final int gapExtend;

    public DNAScorerMatrix(int[][] scores, int gap) {
        this(scores, gap, gap);
    }

    public DNAScorerMatrix(int[][] scores, int gapOpen, int gapExtend) {
        if ( scores == null || scores.length != N+1 || scores[0].length != N+1 ) {
            throw new IllegalArgumentException("Bad globalScores: "+scores);
        }
        this.scores = scores;
        this.gapOpen = gapOpen;
        this.gapExtend = gapExtend;
    }

    public DNAScorerMatrix(int similarity, int transition, int transversion, int gap) {
        this(toScores(similarity, transition, transversion), gap, gap);
    }

    public DNAScorerMatrix(int similarity, int transition, int transversion, int gapOpen, int gapExtend) {
        this(toScores(similarity, transition, transversion), gapOpen, gapExtend);
    }

    public DNAScorerMatrix(String name, int gap) {
        this(MatrixScorer.getStandard(name, DNASeq.TYPE), gap, gap);
    }

    public DNAScorerMatrix(String name, int gapOpen, int gapExtend) {
        this(MatrixScorer.getStandard(name, DNASeq.TYPE), gapOpen, gapExtend);
    }

    private static int[][] toScores(int similarity, int transition, int transversion) {
        int[][] scores = new int[5][5];
        int min = Math.min(similarity, Math.min(transition, transversion));
        for (int i = 0; i < 4; i++) {
            scores[i][i] = similarity;
            scores[i][4] = scores[4][i] = min;
        }
        scores[4][4] = 1;

        scores[DNASeq.interpret('A')][DNASeq.interpret('G')] =
        scores[DNASeq.interpret('G')][DNASeq.interpret('A')] =
        scores[DNASeq.interpret('C')][DNASeq.interpret('T')] =
        scores[DNASeq.interpret('T')][DNASeq.interpret('C')] =
                transition;

        scores[DNASeq.interpret('A')][DNASeq.interpret('T')] =
        scores[DNASeq.interpret('T')][DNASeq.interpret('A')] =
        scores[DNASeq.interpret('A')][DNASeq.interpret('C')] =
        scores[DNASeq.interpret('C')][DNASeq.interpret('A')] =
        scores[DNASeq.interpret('G')][DNASeq.interpret('T')] =
        scores[DNASeq.interpret('T')][DNASeq.interpret('G')] =
        scores[DNASeq.interpret('G')][DNASeq.interpret('C')] =
        scores[DNASeq.interpret('C')][DNASeq.interpret('G')] =
                transversion;

        return scores;
    }

    @Override
    public int s(char a, char b) {
        return scores[DNASeq.interpret(a)][DNASeq.interpret(b)];
    }

    @Override
    public int s(int a, int b) {
        return scores[a][b];
    }

    @Override
    public int min(char a) {
        return scores[DNASeq.interpret(a)][N];
    }

    @Override
    public int min(int a) {
        return scores[a][N];
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
