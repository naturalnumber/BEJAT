package sequence;

public class BEJATDNASeq extends BEJATNSeq {

    private static final char[] LEXICON = {'G', 'C', 'A', 'T'};
    private static final String[] LEXICON_SA = {"G", "C", "A", "T"};
    private static final String LEXICON_S = "GCAT";

    public BEJATDNASeq(String header, String sequence) {
        super(header, sequence);
    }

    public BEJATDNASeq(String header, String[] sequence) {
        super(header, sequence);
    }

    @Override
    public byte charToBinary(char c) {
        return interpret(c);
    }

    public static byte interpret(char c) {
        switch (c) {
            case 'G':
            case 'g':
                return 0;
            case 'C':
            case 'c':
                return 1;
            case 'A':
            case 'a':
                return 2;
            case 'T':
            case 't':
                return 3;
            default:
                throw new IllegalArgumentException("Invalid character: "+c);
        }
    }

    public char[] getLexicon() {
        return LEXICON;
    }
    public String[] getLexiconAsStrings() {
        return LEXICON_SA;
    }
    public String getLexiconAsString() {
        return LEXICON_S;
    }

}
