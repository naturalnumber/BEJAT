package sequence;

public abstract class NSeq extends Seq {

    public static final byte ELEMENT_SIZE = 2;
    public static final byte LEXICON_LENGTH = 4;
    private static final byte[] TRANSLATION = {0, 1, 2, 3};

    protected NSeq(String header, String sequence) {
        super(header, sequence);
    }

    protected NSeq(String header, String[] sequence) {
        super(header, sequence);
    }

    public byte[] getTranslations() {
        return TRANSLATION;
    }
    public byte getElementSize() {
        return ELEMENT_SIZE;
    }

    public int charToBinary(char c) {
        return interpret(c);
    }

    public long getLexiconLength() {
        return LEXICON_LENGTH;
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
            case 'T':
            case 't':
            case 'U':
            case 'u':
                return 3;
            default:
                throw new IllegalArgumentException("Invalid character: "+c);
        }
    }

    /*public long[] convert(String input) {
        byte bits = getBits();
        byte density = getDensity();

        int n = input.length();

        int l = n/32 + ((n%32 > 0) ? 1 : 0);

        long[] output = new long[l];

        int sj = 0;
        long temp;

        for (int i = 0; i < l; i++) {
            temp = 0;
            for (int j = 0; j < 32; j++) {
                temp = (temp << 2) | (charToBinary(input.charAt(sj++))); // mask &
            }
            output[i] = temp;
        }

        return output;
    }//*/

    /*public long[] convert(String... input) {
        byte bits = ELEMENT_SIZE;
        byte density = (byte) (WORD_SIZE / bits);

        //byte mask = 1;

        //for (int i = 1; i < bits; i++) mask = (byte) (mask << 1 + 1);

        int n = 0;

        for (String s : input) n += s.length();

        int l = n/density + ((n%density > 0) ? 1 : 0);

        long[] output = new long[l];

        int si = 0, sj = 0, sl = input[0].length();
        char c;
        long temp;

        for (int i = 0; i < l; i++) {
            temp = 0;
            for (int j = 0; j < density; j++) {
                c = input[si].charAt(sj);

                temp = (temp << bits) | (charToBinary(c)); // mask &

                if (++sj > sl) {
                    sj = 0;
                    sl = input[++si].length();
                }
            }
            output[i] = temp;
        }

        return output;
    }//*/
}
