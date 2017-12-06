package test;

import java.util.Arrays;

import alignment.Alignment;
import dp.DPAligner;
import score.PScorerSimple;
import score.Scorer;
import sequence.PSeq;
import sequence.Seq;

public class PTestSimple3 {

    public static void main(String[] args) {
        Seq first = new PSeq("Test1", "ARNDCQEGHILKMFPSTWYVBJZX");
        Seq second = new PSeq("Test2", "ANDCOEGHELPKMFTPWYVBJDZX");

        Scorer scorer = new PScorerSimple(5, -3, -4);

        DPAligner aligner = new DPAligner(first, second, scorer, true, true);

        aligner.run();


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
