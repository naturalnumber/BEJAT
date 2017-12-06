package score;

import sequence.Seq;

public abstract class Scorer {
    public abstract int s(char a, char b);
    public abstract int s(int a, int b);
    public abstract int w(int l);
    public abstract String getType();
    public abstract boolean sameType(Seq seq);
    public abstract boolean isSimpleGap();
    public abstract boolean isSimpleExtension();

    //  Should override
    public int[][] initializeGlobal(int[][] globalScores) {
        int min = Math.min(globalScores.length, globalScores[0].length);
        int max = Math.max(globalScores.length, globalScores[0].length);
        boolean yLonger = globalScores.length > globalScores[0].length;

        int i = 1;
        for (; i < min; i++) {
            globalScores[i][0] = globalScores[0][i] = w(i-1);
        }

        if (yLonger) for (; i < max; i++) globalScores[i][0] = w(i-1);
        else for (; i < max; i++) globalScores[0][i] = w(i-1);

        return globalScores;
    }
}
