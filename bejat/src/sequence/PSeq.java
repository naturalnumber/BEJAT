package sequence;

public class PSeq extends Seq {

    public static final char[]   LEXICON_CHARS   = {'A', 'R', 'N', 'D', 'C',
                                                    'Q', 'E', 'G', 'H', 'I',
                                                    'L', 'K', 'M', 'F', 'P',
                                                    'S', 'T', 'W', 'Y', 'V',
                                                    'B', 'J', 'Z', 'X'};
    public static final String[] LEXICON_STRINGS = {"A", "R", "N", "D", "C",
                                                    "Q", "E", "G", "H", "I",
                                                    "L", "K", "M", "F", "P",
                                                    "S", "T", "W", "Y", "V",
                                                    "B", "J", "Z", "X"};
    public static final String   LEXICON_STRING  = "ARNDCQEGHILKMFPSTWYVBJZX";
    public static final byte ELEMENT_SIZE = 5;
    public static final byte LEXICON_LENGTH = 24;
    private static final byte[] TRANSLATION = { 0,  1,  2,  3,  4,
                                                5,  6,  7,  8,  9,
                                               10, 11, 12, 13, 14,
                                               15, 16, 17, 18, 19,
                                               20, 21, 22, 23};
    public static final String   TYPE  = "PROTEIN";

    public PSeq(String header, String sequence) {
        super(header, sequence);
    }

    public PSeq(String header, String[] sequence) {
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
            case 'A':
            case 'a':
                return 0;
            case 'R':
            case 'r':
                return 1;
            case 'N':
            case 'n':
                return 2;
            case 'D':
            case 'd':
                return 3;
            case 'C':
            case 'c':
                return 4;
            case 'Q':
            case 'q':
                return 5;
            case 'E':
            case 'e':
                return 6;
            case 'G':
            case 'g':
                return 7;
            case 'H':
            case 'h':
                return 8;
            case 'I':
            case 'i':
                return 9;
            case 'L':
            case 'l':
                return 10;
            case 'K':
            case 'k':
                return 11;
            case 'M':
            case 'm':
                return 12;
            case 'F':
            case 'f':
                return 13;
            case 'P':
            case 'p':
                return 14;
            case 'S':
            case 's':
                return 15;
            case 'T':
            case 't':
                return 16;
            case 'W':
            case 'w':
                return 17;
            case 'Y':
            case 'y':
                return 18;
            case 'V':
            case 'v':
                return 19;
            case 'B':
            case 'b':
                return 20;
            case 'J':
            case 'j':
                return 21;
            case 'Z':
            case 'z':
                return 22;
            case 'X':
            case 'x':
                return 23;
            //case MATRIX_PAD:
            //    return 24;
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
    public byte[] getTranslations() {
        return TRANSLATION;
    }

    @Override
    public byte getElementSize() {
        return ELEMENT_SIZE;
    }

    @Override
    public long getLexiconLength() {
        return LEXICON_LENGTH;
    }

    @Override
    public String getLexiconAsString() {
        return LEXICON_STRING;
    }

}
