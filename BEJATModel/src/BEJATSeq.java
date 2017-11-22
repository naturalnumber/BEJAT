import java.security.InvalidParameterException;

public abstract class BEJATSeq {

    public static final byte WORD_SIZE = 64;

    protected final String sequence;
    protected final long[] data;

    protected BEJATSeq(String sequence) {
        if (sequence == null) throw new NullPointerException("No sequence");
        this.sequence = sequence;
        this.data = convert(sequence);
    }

    protected BEJATSeq(String[] sequence) {
        if (sequence == null) throw new NullPointerException("No sequence");
        StringBuilder sb = new StringBuilder(sequence[0]);
        for (int i = 1; i < sequence.length; i++) {
            sb.append(sequence[i]);
        }
        this.sequence = sb.toString();
        this.data = convert(this.sequence);
    }

    public String getSequence() {
        return sequence;
    }

    public long[] getData() {
        return data;
    }

    public abstract char[] getLexicon();
    public abstract String[] getLexiconAsString();
    public abstract byte[] getTranslations();
    public abstract byte getElementSize();

    public abstract byte charToBinary(char c);
    public abstract long[] convert(String... input);
    public abstract long[] convert(String input);

    public static long[] convert(byte size, BEJATSeq type, String... input) {
        byte bits = size;
        byte density = (byte) (WORD_SIZE / bits);
        if (density < 1) throw new InvalidParameterException("Invalid size: "+bits);

        //byte mask = 1;

        //for (int i = 1; i < bits; i++) mask = (byte) (mask << 1 + 1); //TODO: Check this

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

                temp = (temp << bits) | (type.charToBinary(c)); // mask &

                if (++sj > sl) {
                    sj = 0;
                    sl = input[++si].length();
                }
            }
            output[i] = temp;
        }

        return output;
    }

    public static long[] convert(byte size, BEJATSeq type, String input) {
        byte bits = size;
        byte density = (byte) (WORD_SIZE / bits);
        if (density < 1) throw new InvalidParameterException("Invalid size: "+bits);

        //byte mask = 1;

        //for (int i = 1; i < bits; i++) mask = (byte) (mask << 1 + 1);

        int n = input.length();

        int l = n/density + ((n%density > 0) ? 1 : 0);

        long[] output = new long[l];

        int sj = 0;
        long temp;

        for (int i = 0; i < l; i++) {
            temp = 0;
            for (int j = 0; j < density; j++) {
                temp = (temp << bits) | (type.charToBinary(input.charAt(sj++))); // mask &
            }
            output[i] = temp;
        }

        return output;
    }
}
