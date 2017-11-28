package sequence;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.stream.IntStream;

public abstract class BEJATSeq implements CharSequence, Serializable, Cloneable {

    public static final byte WORD_SIZE = 64;
    public static final char INVALID = '?';

    protected final String header;
    protected final String sequence;
    protected final long[] data;
    protected final int length;
    protected final byte bits;
    protected final byte density;
    protected final long mask;

    protected BEJATSeq(String header, String sequence) {
        this.header = header;
        this.sequence = clean(this, sequence);
        this.length = this.sequence.length();
        this.data = convert(this.sequence);

        this.bits = getElementSize();
        this.density = (byte) (WORD_SIZE / bits);
        if (density < 1) throw new InvalidParameterException("Invalid size: "+getElementSize());

        this.mask = (1 << bits) - 1;
    }

    protected BEJATSeq(String header, String[] sequence) {
        this.header = header;
        this.sequence = clean(this, sequence);
        this.length = this.sequence.length();
        this.data = convert(this.sequence);

        this.bits = getElementSize();
        this.density = (byte) (WORD_SIZE / bits);
        if (density < 1) throw new InvalidParameterException("Invalid size: "+getElementSize());

        this.mask = (1 << bits) - 1;
    }

    //  Getters
    public String getHeader() {
        return header;
    }
    public String getSequence() {
        return sequence;
    }
    public long[] getData() {
        return data;
    }
    public byte getBits() {
        return bits;
    }
    public byte getDensity() {
        return density;
    }
    public long getMask() {
        return mask;
    }
    @Override
    public int length() {
        return length;
    }
    @Override
    public char charAt(int index) {
        return (validPosition(index)) ? sequence.charAt(index) : INVALID;
    }

    //  Subclass methods
    protected char charAtFast(int index) {
        return sequence.charAt(index);
    }

    //  Helper methods
    protected boolean validPosition(int i) {
        return i >= 0 && i < length();
    }

    //  Abstract methods
    public abstract char[] getLexicon();
    public abstract String[] getLexiconAsStrings();
    public abstract byte[] getTranslations();
    public abstract byte getElementSize();
    public abstract byte charToBinary(char c);

    //  Should override
    public String getLexiconAsString() {
        char[] lexicon = getLexicon();
        StringBuilder lexiconString = new StringBuilder(lexicon.length);
        for (char c : lexicon) lexiconString.append(Character.toUpperCase(c));
        return lexiconString.toString();
    }
    public long[] convert(String input) {
        return convert(this, input);
    }

    //  Helpers
    protected static long[] convert(BEJATSeq type, String input) {
        byte bits = type.getElementSize();
        byte density = type.getDensity();
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
    protected static String clean(BEJATSeq type, String... sequence) {
        if (sequence == null || sequence.length < 1 || sequence[0].length() < 1) {
            throw new InvalidParameterException("No sequence");
        }
        char[] lexicon = type.getLexicon();

        StringBuilder regexBuilder = new StringBuilder(lexicon.length+3);
        regexBuilder.append("[^").append(type.getLexiconAsString()).append(']');

        String regex = regexBuilder.toString();

        StringBuilder sb = new StringBuilder(sequence.length * sequence[0].length());
        sb.append(sequence[0].toUpperCase().replaceAll(regex, ""));
        for (int i = 1; i < sequence.length; i++) {
            sb.append(sequence[i]);
        }
        return sb.toString();
    }

    //  CharSequence methods
    @Override
    public CharSequence subSequence(int start, int end) {
        return sequence.subSequence(start, end); // TODO: Make same class?
    }
    @Override
    public IntStream chars() {
        return sequence.chars();
    }
    @Override
    public IntStream codePoints() {
        return sequence.codePoints();
    }

    /*public static long[] convert(sequence.BEJATSeq type, String... input) {
        byte bits = type.getElementSize();
        byte density = type.getDensity();
        if (density < 1) throw new InvalidParameterException("Invalid size: "+bits);

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

                temp = (temp << bits) | (type.charToBinary(c)); // mask &

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
