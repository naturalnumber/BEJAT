package test;

import java.io.FileOutputStream;
import java.util.Arrays;

import alignment.Alignment;
import dp.DPAligner;
import score.DNAScorerMatrix;
import score.DNAScorerSimple;
import score.Scorer;
import sequence.DNASeq;
import sequence.Seq;

public class DNATestMatrix1 {

    public static void main(String[] args) {
        String name = "DNATestMatrix1";

        Seq first = new DNASeq("Test1", "GAATTCAGTTA");
        Seq second = new DNASeq("Test2", "GGATCGA");

        Scorer scorer = new DNAScorerMatrix("BLAST", -9);

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
