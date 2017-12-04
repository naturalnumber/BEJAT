import java.util.Arrays;

import alignment.Alignment;
import dp.DPAligner;
import score.DNAScorerSimple;
import score.Scorer;
import sequence.DNASeq;
import sequence.Seq;

public class testRun {

    public static void main(String[] args) {
        Seq first = new DNASeq("Test1", "GAATTCAGTTA");
        Seq second = new DNASeq("Test2", "GGATCGA");

        Scorer scorer = new DNAScorerSimple(5, -3, -4);

        DPAligner aligner = new DPAligner(first, second, scorer, true, true);

        aligner.run();

        /*
        System.out.println("Global Scores: "+aligner.getGlobalScore());

        int[][] scores = aligner.getGlobalScores();

        for (int i = 0; i <= second.length(); i++) {
            System.out.println(Arrays.toString(scores[i]));
        }

        System.out.println("Local Scores: "+aligner.getLocalScore());

        scores = aligner.getLocalScores();

        for (int i = 0; i <= second.length(); i++) {
            System.out.println(Arrays.toString(scores[i]));
        }//*/

        System.out.println("Global Alignments: "+aligner.getGlobalScore());

        for (Alignment a : aligner.getGlobalAlignments()) {
            System.out.println(a);
        }

        System.out.println("Local Alignments: "+aligner.getLocalScore());

        for (Alignment a : aligner.getLocalAlignments()) {
            System.out.println(a);
        }
    }
}
