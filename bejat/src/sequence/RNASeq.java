package sequence;

public class RNASeq extends NSeq {

    public static final char[]   LEXICON_CHARS   = {'G', 'C', 'A', 'U'};
    public static final String[] LEXICON_STRINGS = {"G", "C", "A", "U"};
    public static final String   LEXICON_STRING  = "GCAU";
    public static final String   TYPE  = "RNA";

    public RNASeq(String header, String sequence) {
        super(header, sequence);
    }

    public RNASeq(String header, String[] sequence) {
        super(header, sequence);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int charToBinary(char c) {
        return interpret(c);
    }

    public static int interpret(char c) {
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
            case 'U':
            case 'u':
                return 3;
            //case MATRIX_PAD:
            //    return 4;
            default:
                throw new IllegalArgumentException("Invalid character: "+c);
        }
    }

    @Override
    public char[] getLexicon() {
        return LEXICON_CHARS;
    }

    @Override
    public String[] getLexiconAsStrings() {
        return LEXICON_STRINGS;
    }

    @Override
    public String getLexiconAsString() {
        return LEXICON_STRING;
    }

}
