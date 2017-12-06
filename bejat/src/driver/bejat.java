package driver;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import score.Scorer;
import sequence.Seq;

public class bejat {
    private static final String DELIM = "=";
    private static final String SEPARATOR = ",";
    private static final String P_DO_GLOBAL = "GLOBAL";
    private static final String P_DO_LOCAL = "LOCAL";
    private static final String P_TYPE = "TYPE";
    private static final String P_METHOD = "METHOD";
    private static final String P_VALUE = "VALUE";
    private static final String T_DNA = "DNA";
    private static final String T_PROTEIN = "PROTEIN";
    private static final String M_SIMPLE = "SIMPLE";
    private static final String M_MATRIX = "MATRIX";

    public static void main(String[] args) {
        ArrayList<Seq> sequences = new ArrayList<Seq>();
        Scorer scorer;
        String type, method = null, input, caps, prev = "", argument;
        boolean global = false, local = false, hasPrev = false;

        for (String file : args) try (Scanner in = new Scanner(new InputStreamReader(new FileInputStream(file)))) {
            while (in.hasNext() || hasPrev) {
                if (hasPrev) {
                    input = prev;
                    hasPrev = false;
                } else {
                    input = in.nextLine();
                }
                caps = input.toUpperCase();

                if (caps.contains(DELIM)) {
                    argument = caps.substring(caps.indexOf(DELIM)+1);
                    if (caps.startsWith(P_DO_GLOBAL)) {
                        global = Boolean.valueOf(argument);
                    } else if (caps.startsWith(P_DO_LOCAL)) {
                        local = Boolean.valueOf(argument);
                    } else if (caps.startsWith(P_TYPE)) {
                        type = argument.trim();
                    } else if (caps.startsWith(P_METHOD)) {
                        method = argument.trim();
                    } else if (caps.startsWith(P_VALUE)) {
                        if (method == null || method.equalsIgnoreCase("")) {
                            System.out.println("Ignoring out of order parameter: "+input);
                        }
                        if (method.equalsIgnoreCase(M_SIMPLE)) {
                            // TODO: HERE
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Issue with: "+file+" "+e.getMessage());
        }
    }
}
