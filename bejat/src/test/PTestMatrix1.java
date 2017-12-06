package test;

import java.io.FileOutputStream;
import java.util.Arrays;

import alignment.Alignment;
import dp.DPAligner;
import score.PScorerMatrix;
import score.PScorerSimple;
import score.Scorer;
import sequence.PSeq;
import sequence.Seq;

public class PTestMatrix1 {

    public static void main(String[] args) {
        String name = "PTestMatrix1";

        Seq first = new PSeq("Test1", "GAATTCAGTTA");
        Seq second = new PSeq("Test2", "GGATCGA");

        Scorer scorer = new PScorerMatrix("BLOSUM45", -10);

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


        System.out.print("Printing files");

        try {
            aligner.printGlobalScores(new FileOutputStream("./bejat/src/test/out/"+name+"Global.csv", false), ",", "\n");
            System.out.print(".");
            aligner.printGlobalAdjacency(new FileOutputStream("./bejat/src/test/out/"+name+"GlobalAdj.csv", false), ",", "\n");
            System.out.print(".");
            aligner.printGlobalAlignment(new FileOutputStream("./bejat/src/test/out/"+name+"GlobalAlign.txt", false), " ", "\n");
            System.out.print(".");
            aligner.printLocalScores(new FileOutputStream("./bejat/src/test/out/"+name+"Local.csv", false), ",", "\n");
            System.out.print(".");
            aligner.printLocalAdjacency(new FileOutputStream("./bejat/src/test/out/"+name+"LocalAdj.csv", false), ",", "\n");
            System.out.print(".");
            aligner.printLocalAlignment(new FileOutputStream("./bejat/src/test/out/"+name+"LocalAlign.txt", false), " ", "\n");
            System.out.print(".");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(" Done!");
    }
}
