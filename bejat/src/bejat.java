import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import dp.DPAligner;
import javafx.util.Pair;
import score.DNAScorerMatrix;
import score.DNAScorerSimple;
import score.MatrixScorer;
import score.PScorerMatrix;
import score.PScorerSimple;
import score.RNAScorerMatrix;
import score.RNAScorerSimple;
import score.Scorer;
import sequence.DNASeq;
import sequence.PSeq;
import sequence.RNASeq;
import sequence.Seq;

public class bejat {
    private static final boolean DEBUG = false;
    private static final String DELIM             = "=";
    private static final String SEPARATOR         = ",";
    private static final String HEADER_FLAG       = ">";
    private static final String COMMENT_FLAG       = "#";
    private static final String P_DO_GLOBAL       = "GLOBAL";
    private static final String P_DO_LOCAL        = "LOCAL";
    private static final String P_PRINT_ALIGNMENT = "ALIGNMENT";
    private static final String P_PRINT_SCORES    = "SCORES";
    private static final String P_PRINT_ADJACENCY = "ADJACENCY";
    private static final String P_PRINT_ARROWS    = "ARROWS";
    private static final String P_TYPE            = "TYPE";
    private static final String P_VALUE           = "VALUE";
    private static final String T_DNA             = "DNA";
    private static final String T_RNA             = "RNA";
    private static final String T_PROTEIN         = "PROTEIN";

    public static void main(String[] args) {
        ArrayList<Seq>                     sequences       = new ArrayList<>();
        ArrayList<Pair<String, DPAligner>> aligners        = new ArrayList<>();
        Scorer                             scorer          = null;
        String                             type            = null, input, caps, prev = "", argument;
        boolean                            global          = false;
        boolean                            local           = false;
        boolean                            hasPrev         = false;
        boolean                            printAlignments = true;
        boolean                            printScores     = false;
        boolean                            printAdjacency  = false;
        boolean                            printArrows     = false;

        int comment;

        if (args.length < 1) {
            System.out.println("Input file expected... Please provide list of input files:");
            String commands;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
                commands = in.readLine();
            } catch (Exception e) {
                if (DEBUG) e.printStackTrace();
                System.out.println("Unknown input...");
                commands = null;
            }

            if (commands == null || commands.length() < 1) {
                System.out.println("Unable to proceed without input...");
                System.exit(0);
            }

            args = commands.split(" ");
        }

        for (String file : args)
            try (Scanner in = new Scanner(new InputStreamReader(new FileInputStream(file)))) {
                while (in.hasNext() || hasPrev) {
                    if (hasPrev) {
                        input = prev;
                        hasPrev = false;
                    } else {
                        input = in.nextLine();
                    }
                    caps = input.toUpperCase();

                    comment = caps.indexOf(COMMENT_FLAG);
                    if (comment > 0) {
                        input = input.substring(0, comment);
                        caps = caps.substring(0, comment);
                    }

                    if (caps.trim().equalsIgnoreCase("")) continue;

                    if (caps.contains(DELIM)) {
                        argument = caps.substring(caps.indexOf(DELIM) + 1).replaceAll(" ", "");
                        if (caps.startsWith(P_DO_GLOBAL)) {
                            global = Boolean.valueOf(argument);
                        } else if (caps.startsWith(P_DO_LOCAL)) {
                            local = Boolean.valueOf(argument);
                        } else if (caps.startsWith(P_PRINT_ALIGNMENT)) {
                            printAlignments = Boolean.valueOf(argument);
                        } else if (caps.startsWith(P_PRINT_SCORES)) {
                            printScores = Boolean.valueOf(argument);
                        } else if (caps.startsWith(P_PRINT_ADJACENCY)) {
                            printAdjacency = Boolean.valueOf(argument);
                        } else if (caps.startsWith(P_PRINT_ARROWS)) {
                            printArrows = Boolean.valueOf(argument);
                        } else if (caps.startsWith(P_TYPE)) {
                            if (type != null && type.length() > 0 &&
                                !type.equalsIgnoreCase(argument.trim())) {
                                System.out.println("Type overwrite error: " + input);
                            } else {
                                type = argument.trim();
                            }
                        } else if (caps.startsWith(P_VALUE)) {
                            String[] arguments = argument.split(SEPARATOR);
                            int      n         = arguments.length;
                            switch (n) {
                                default:
                                    System.out.println("Invalid scoring scheme: " + input);
                                    continue;
                                case 2:
                                    try {
                                        if (MatrixScorer.isPScoreMatrixName(arguments[0])) {
                                            type = T_PROTEIN;
                                            scorer = new PScorerMatrix(arguments[0],
                                                                       Integer.parseInt(
                                                                               arguments[1]));
                                        } else if (MatrixScorer
                                                           .isDNAScoreMatrixName(arguments[0])) {
                                            type = T_DNA;
                                            scorer = new DNAScorerMatrix(arguments[0],
                                                                         Integer.parseInt(
                                                                                 arguments[1]));
                                        } else if (MatrixScorer
                                                           .isRNAScoreMatrixName(arguments[0])) {
                                            type = T_RNA;
                                            scorer = new RNAScorerMatrix(arguments[0],
                                                                         Integer.parseInt(
                                                                                 arguments[1]));
                                        } else {
                                            System.out.println(
                                                    "Invalid scoring matrix: " + arguments[0]);
                                            continue;
                                        }
                                    } catch (NumberFormatException nfe) {
                                        System.out
                                                .println("Invalid gap parameter: " + arguments[1]);
                                        continue;
                                    }
                                    break;
                                case 3:
                                    try {
                                        if (MatrixScorer.isPScoreMatrixName(arguments[0])) {
                                            type = T_PROTEIN;
                                            scorer = new PScorerMatrix(arguments[0],
                                                                       Integer.parseInt(
                                                                               arguments[1]),
                                                                       Integer.parseInt(
                                                                               arguments[2]));
                                        } else if (MatrixScorer
                                                           .isDNAScoreMatrixName(arguments[0])) {
                                            type = T_DNA;
                                            scorer = new DNAScorerMatrix(arguments[0],
                                                                         Integer.parseInt(
                                                                                 arguments[1]),
                                                                         Integer.parseInt(
                                                                                 arguments[2]));
                                        } else if (MatrixScorer
                                                           .isRNAScoreMatrixName(arguments[0])) {
                                            type = T_RNA;
                                            scorer = new RNAScorerMatrix(arguments[0],
                                                                         Integer.parseInt(
                                                                                 arguments[1]),
                                                                         Integer.parseInt(
                                                                                 arguments[2]));
                                        } else try {
                                            if (type == null || type.length() == 0) {
                                                System.out.println(
                                                        "Must set type (DNA, RNA, PROTEIN) before scoring scheme: " +
                                                        argument);
                                                continue;
                                            }
                                            switch (type) {
                                                case T_PROTEIN:
                                                    scorer = new PScorerSimple(Integer.parseInt(
                                                            arguments[0]),
                                                                               Integer.parseInt(
                                                                                       arguments[1]),
                                                                               Integer.parseInt(
                                                                                       arguments[2]));
                                                    break;
                                                case T_DNA:
                                                    scorer = new DNAScorerSimple(Integer.parseInt(
                                                            arguments[0]),
                                                                                 Integer.parseInt(
                                                                                         arguments[1]),
                                                                                 Integer.parseInt(
                                                                                         arguments[2]));
                                                    break;
                                                case T_RNA:
                                                    scorer = new RNAScorerSimple(Integer.parseInt(
                                                            arguments[0]),
                                                                                 Integer.parseInt(
                                                                                         arguments[1]),
                                                                                 Integer.parseInt(
                                                                                         arguments[2]));
                                                    break;
                                                default:
                                                    System.out.println(
                                                            "Unable to parse scoring scheme due to invalid type: " +
                                                            type);
                                                    continue;
                                            }
                                        } catch (NumberFormatException nfe) {
                                            System.out
                                                    .println("Invalid parameters: " + arguments[0] +
                                                             SEPARATOR + arguments[1] +
                                                             SEPARATOR + arguments[2] +
                                                             ": " + nfe.getMessage());
                                            continue;
                                        }
                                    } catch (NumberFormatException nfe) {
                                        System.out
                                                .println("Invalid gap parameters: " + arguments[1] +
                                                         SEPARATOR + arguments[2] +
                                                         ": " + nfe.getMessage());
                                        continue;
                                    }
                                    break;
                                case 4:
                                    try {
                                        if (type == null || type.length() == 0) {
                                            System.out.println(
                                                    "Must set type (DNA, RNA, PROTEIN) before scoring scheme: " +
                                                    argument);
                                            continue;
                                        }
                                        switch (type) {
                                            case T_PROTEIN:
                                                scorer = new PScorerSimple(Integer.parseInt(
                                                        arguments[0]),
                                                                           Integer.parseInt(
                                                                                   arguments[1]),
                                                                           Integer.parseInt(
                                                                                   arguments[2]),
                                                                           Integer.parseInt(
                                                                                   arguments[3]));
                                                break;
                                            case T_DNA:
                                                scorer = new DNAScorerSimple(Integer.parseInt(
                                                        arguments[0]),
                                                                             Integer.parseInt(
                                                                                     arguments[1]),
                                                                             Integer.parseInt(
                                                                                     arguments[2]),
                                                                             Integer.parseInt(
                                                                                     arguments[3]));
                                                break;
                                            case T_RNA:
                                                scorer = new RNAScorerSimple(Integer.parseInt(
                                                        arguments[0]),
                                                                             Integer.parseInt(
                                                                                     arguments[1]),
                                                                             Integer.parseInt(
                                                                                     arguments[2]),
                                                                             Integer.parseInt(
                                                                                     arguments[3]));
                                                break;
                                            default:
                                                System.out.println(
                                                        "Unable to parse scoring scheme due to invalid type: " +
                                                        type);
                                                continue;
                                        }
                                    } catch (NumberFormatException nfe) {
                                        System.out.println("Invalid parameters: " + arguments[0] +
                                                           SEPARATOR + arguments[1] +
                                                           SEPARATOR + arguments[2] +
                                                           SEPARATOR + arguments[3] +
                                                           ": " + nfe.getMessage());
                                        continue;
                                    }
                                    break;
                                case 5:
                                    try {
                                        if (type == null || type.length() == 0) {
                                            System.out.println(
                                                    "Must set type (DNA, RNA) before scoring scheme: " +
                                                    argument);
                                            continue;
                                        }
                                        switch (type) {
                                            case T_DNA:
                                                scorer = new DNAScorerMatrix(Integer.parseInt(
                                                        arguments[0]),
                                                                             Integer.parseInt(
                                                                                     arguments[1]),
                                                                             Integer.parseInt(
                                                                                     arguments[2]),
                                                                             Integer.parseInt(
                                                                                     arguments[3]),
                                                                             Integer.parseInt(
                                                                                     arguments[4]));
                                                break;
                                            case T_RNA:
                                                scorer = new RNAScorerMatrix(Integer.parseInt(
                                                        arguments[0]),
                                                                             Integer.parseInt(
                                                                                     arguments[1]),
                                                                             Integer.parseInt(
                                                                                     arguments[2]),
                                                                             Integer.parseInt(
                                                                                     arguments[3]),
                                                                             Integer.parseInt(
                                                                                     arguments[4]));
                                                break;
                                            case T_PROTEIN:
                                                System.out.println(
                                                        "Unable to parse scoring scheme for PROTEIN type: " +
                                                        argument);
                                                continue;
                                            default:
                                                System.out.println(
                                                        "Unable to parse scoring scheme due to invalid type: " +
                                                        type);
                                                continue;
                                        }
                                    } catch (NumberFormatException nfe) {
                                        System.out.println("Invalid parameters: " + arguments[0] +
                                                           SEPARATOR + arguments[1] +
                                                           SEPARATOR + arguments[2] +
                                                           SEPARATOR + arguments[3] +
                                                           SEPARATOR + arguments[4] +
                                                           ": " + nfe.getMessage());
                                        continue;
                                    }
                                    break;
                                case 11:
                                    try {
                                        if (type == null || type.length() == 0) {
                                            System.out.println(
                                                    "Must set type (DNA, RNA) before scoring scheme: " +
                                                    argument);
                                            continue;
                                        }
                                        int[][] scores = MatrixScorer.getNScoreFromInts(
                                                Integer.parseInt(arguments[0]),
                                                Integer.parseInt(arguments[1]),
                                                Integer.parseInt(arguments[2]),
                                                Integer.parseInt(arguments[3]),
                                                Integer.parseInt(arguments[4]),
                                                Integer.parseInt(arguments[5]),
                                                Integer.parseInt(arguments[6]),
                                                Integer.parseInt(arguments[7]),
                                                Integer.parseInt(arguments[8]),
                                                Integer.parseInt(arguments[9]));
                                        switch (type) {
                                            case T_DNA:
                                                scorer = new DNAScorerMatrix(scores,
                                                                             Integer.parseInt(
                                                                                     arguments[10]));
                                                break;
                                            case T_RNA:
                                                scorer = new RNAScorerMatrix(scores,
                                                                             Integer.parseInt(
                                                                                     arguments[10]));
                                                break;
                                            case T_PROTEIN:
                                                System.out.println(
                                                        "Unable to parse scoring scheme for PROTEIN type: " +
                                                        argument);
                                                continue;
                                            default:
                                                System.out.println(
                                                        "Unable to parse scoring scheme due to invalid type: " +
                                                        type);
                                                continue;
                                        }
                                    } catch (NumberFormatException nfe) {
                                        System.out.println("Invalid parameters: " + argument +
                                                           ": " + nfe.getMessage());
                                        continue;
                                    }
                                    break;
                                case 12:
                                    try {
                                        if (type == null || type.length() == 0) {
                                            System.out.println(
                                                    "Must set type (DNA, RNA) before scoring scheme: " +
                                                    argument);
                                            continue;
                                        }
                                        int[][] scores = MatrixScorer.getNScoreFromInts(
                                                Integer.parseInt(arguments[0]),
                                                Integer.parseInt(arguments[1]),
                                                Integer.parseInt(arguments[2]),
                                                Integer.parseInt(arguments[3]),
                                                Integer.parseInt(arguments[4]),
                                                Integer.parseInt(arguments[5]),
                                                Integer.parseInt(arguments[6]),
                                                Integer.parseInt(arguments[7]),
                                                Integer.parseInt(arguments[8]),
                                                Integer.parseInt(arguments[9]));
                                        switch (type) {
                                            case T_DNA:
                                                scorer = new DNAScorerMatrix(scores,
                                                                             Integer.parseInt(
                                                                                     arguments[10]),
                                                                             Integer.parseInt(
                                                                                     arguments[11]));
                                                break;
                                            case T_RNA:
                                                scorer = new RNAScorerMatrix(scores,
                                                                             Integer.parseInt(
                                                                                     arguments[10]),
                                                                             Integer.parseInt(
                                                                                     arguments[11]));
                                                break;
                                            case T_PROTEIN:
                                                System.out.println(
                                                        "Unable to parse scoring scheme for PROTEIN type: " +
                                                        argument);
                                                continue;
                                            default:
                                                System.out.println(
                                                        "Unable to parse scoring scheme due to invalid type: " +
                                                        type);
                                                continue;
                                        }
                                    } catch (NumberFormatException nfe) {
                                        System.out.println("Invalid parameters: " + argument +
                                                           ": " + nfe.getMessage());
                                        continue;
                                    }
                                    break;
                                //  End switch
                            }
                        } // if (caps.contains(DELIM)) sub if
                    } else if (caps.startsWith(HEADER_FLAG)) {
                        if (type == null || type.length() == 0) {
                            System.out.println(
                                    "Must set type (DNA, RNA, PROTEIN) before sequence input: " +
                                    input);
                            continue;
                        }
                        String        header   = input.substring(1);
                        StringBuilder sequence = new StringBuilder();
                        String        next     = in.nextLine();
                        while (next != null && !next.startsWith(HEADER_FLAG)) {
                            sequence.append(next);
                            if (in.hasNextLine()) {
                                next = in.nextLine();
                            } else {
                                next = null;
                            }
                        }

                        switch (type) {
                            case T_DNA:
                                sequences.add(new DNASeq(header, sequence.toString()));
                                break;
                            case T_RNA:
                                sequences.add(new RNASeq(header, sequence.toString()));
                                break;
                            case T_PROTEIN:
                                sequences.add(new PSeq(header, sequence.toString()));
                                break;
                            default:
                                System.out.println(
                                        "Unable to parse sequence due to invalid type: " + type);
                                continue;
                        }

                        if (next == null) {
                            break;
                        } else {
                            prev = next;
                            hasPrev = true;
                        }
                    }
                }
            } catch (Exception e) {
                if (DEBUG) e.printStackTrace();
                System.out.println("Issue with: " + file + " " + e.getMessage());
            }

        if (type == null || type.length() == 0) {
            System.out.println("Unable to proceed without type.");
            System.exit(1);
        }
        if (scorer == null) {
            System.out.println("Unable to proceed without scoring scheme.");
            System.exit(1);
        }
        if (sequences.size() < 2) {
            System.out.println("Unable to proceed with " + sequences.size() + " sequence" +
                               ((sequences.size() == 1) ? "" : "s") + ".");
            System.exit(1);
        }

        DPAligner               aligner;
        Seq                     first, second;
        Pair<String, DPAligner> pair;
        String name;

        if ((global || local) && (printAlignments || printScores || printAdjacency)) {
            if (sequences.size() == 2) {
                first = sequences.get(0);
                second = sequences.get(1);
                aligner = new DPAligner(first, second, scorer, global, local);
                aligners.add(new Pair<>(first.getName()+"_"+second.getName(), aligner));
                System.out.print("Aligning pair: "+first.getName()+" & "+second.getName());
                aligner.run();
                System.out.println(" Aligned!");
            } else {
                int n = sequences.size();
                for (int i = 0; i < n; i++) {
                    first = sequences.get(i);

                    for (int j = i+1; j < n; j++) {
                        second = sequences.get(j);

                        aligner = new DPAligner(first, second, scorer, global, local);
                        aligners.add(new Pair<>(first.getName()+"_"+second.getName(), aligner));
                        System.out.print("Aligning pair: "+first.getName()+" & "+second.getName());
                        aligner.run();
                        System.out.println(" Aligned!");
                    }
                }
            }

            for (Pair<String, DPAligner> p : aligners) {
                aligner = p.getValue();
                name = p.getKey();
                first = aligner.getFirst();
                second = aligner.getSecond();
                System.out.print("Generating output for pair: "+first.getName()+" & "+second.getName());
                if (printAlignments) {
                    if (global) {
                        try {
                            aligner.printGlobalAlignment(
                                    new FileOutputStream("./" + name + "GlobalAlign.txt", false),
                                    "", "\n");
                            System.out.print(".");
                        } catch (Exception ignored) {
                            if (DEBUG) ignored.printStackTrace();
                            System.out.print("!");
                        }
                    }
                    if (local) {
                        try {
                            aligner.printLocalAlignment(
                                    new FileOutputStream("./" + name + "LocalAlign.txt", false),
                                    "", "\n");
                            System.out.print(".");
                        } catch (Exception ignored) {
                            if (DEBUG) ignored.printStackTrace();
                            System.out.print("!");
                        }
                    }
                }
                if (printScores) {
                    if (global) {
                        try {
                            aligner.printGlobalScores(
                                    new FileOutputStream("./" + name + "GlobalScores.csv", false),
                                    ",", "\n");
                            System.out.print(".");
                        } catch (Exception ignored) {
                            if (DEBUG) ignored.printStackTrace();
                            System.out.print("!");
                        }
                    }
                    if (local) {
                        try {
                            aligner.printLocalScores(
                                    new FileOutputStream("./" + name + "LocalScores.csv", false),
                                    ",", "\n");
                            System.out.print(".");
                        } catch (Exception ignored) {
                            if (DEBUG) ignored.printStackTrace();
                            System.out.print("!");
                        }
                    }
                }
                if (printAdjacency) {
                    if (global) {
                        try {
                            aligner.printGlobalAdjacency(
                                    new FileOutputStream("./" + name + "GlobalAdjacency.csv", false),
                                    ",", "\n");
                            System.out.print(".");
                        } catch (Exception ignored) {
                            if (DEBUG) ignored.printStackTrace();
                            System.out.print("!");
                        }
                    }
                    if (local) {
                        try {
                            aligner.printLocalAdjacency(
                                    new FileOutputStream("./" + name + "LocalAdjacency.csv", false),
                                    ",", "\n");
                            System.out.print(".");
                        } catch (Exception ignored) {
                            if (DEBUG) ignored.printStackTrace();
                            System.out.print("!");
                        }
                    }
                    if (printArrows) {
                        if (global) {
                            try {
                                aligner.printGlobalArrows(
                                        new FileOutputStream("./" + name + "GlobalArrows.csv",
                                                             false),
                                        ",", "\n");
                                System.out.print(".");
                            } catch (Exception ignored) {
                                if (DEBUG) ignored.printStackTrace();
                                System.out.print("!");
                            }
                        }
                        if (local) {
                            try {
                                aligner.printLocalArrows(
                                        new FileOutputStream("./" + name + "LocalArrows.csv",
                                                             false),
                                        ",", "\n");
                                System.out.print(".");
                            } catch (Exception ignored) {
                                if (DEBUG) ignored.printStackTrace();
                                System.out.print("!");
                            }
                        }
                    }
                }
                System.out.println(" Done!");
            }

            System.out.println();
            System.out.println("Run finished!");
            System.exit(0);
        } else {
            if (global || local) {
                System.out.println("No alignment requested...");
            }
            if (printAlignments || printScores || printAdjacency) {
                System.out.println("No output requested...");
            }
            System.exit(0);
        }
    }
}