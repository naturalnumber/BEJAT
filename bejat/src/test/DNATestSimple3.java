package test;

import java.util.Arrays;

import alignment.Alignment;
import dp.DPAligner;
import score.DNAScorerSimple;
import score.Scorer;
import sequence.DNASeq;
import sequence.Seq;

public class DNATestSimple3 {

    public static void main(String[] args) {
        Seq first = new DNASeq("Wiki1", "TGTTACGG");
        Seq second = new DNASeq("Wiki2", "GGTTGACTA");

        Scorer scorer = new DNAScorerSimple(3, -3, -2);

        DPAligner aligner = new DPAligner(first, second, scorer, false, true);

        aligner.run();

        /*
        System.out.println("Global Scores: "+aligner.getGlobalScore());

        int[][] scores = aligner.getGlobalScores();

        for (int i = 0; i <= second.length(); i++) {
            System.out.println(Arrays.toString(scores[i]));
        }//*/


        System.out.println("Local Scores: "+aligner.getLocalScore());

        int[][] scores = aligner.getLocalScores();

        for (int i = 0; i <= second.length(); i++) {
            System.out.println(Arrays.toString(scores[i]));
        }//*/

        /*
        System.out.println("Global Alignments: "+aligner.getGlobalScore());

        for (Alignment a : aligner.getGlobalAlignments()) {
            System.out.println(a);
        }//*/

        System.out.println("Local Alignments: "+aligner.getLocalScore());

        for (Alignment a : aligner.getLocalAlignments()) {
            System.out.println(a);
        }//*/
    }
}
