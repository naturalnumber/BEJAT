package sequence;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.stream.IntStream;

public abstract class Seq implements CharSequence, Serializable, Cloneable {

    public static final byte WORD_SIZE = 64;
    public static final char INVALID = '?';
    public static final char MATRIX_PAD = '*';

    protected final String header;
    protected final String sequence;
    protected final String name;
    protected final int length;
    //protected final byte bits;
    //protected final byte density;
    //protected final long mask;
    //protected final long[] data;

    protected Seq(String header, String sequence) {
        if (sequence == null) throw new IllegalArgumentException("Null sequence");

        this.sequence = clean(this, sequence);
        this.length = this.sequence.length();
        this.header = (header != null) ? header : Integer.toString(this.length);
        int i = header.indexOf(' ');
        this.name = nameClean((i > 0) ? header.substring(0, i) : header);
        //this.data = convert(this.sequence);

        //this.bits = getElementSize();
        //this.density = (byte) (WORD_SIZE / bits);
        //if (density < 1) throw new InvalidParameterException("Invalid size: "+getElementSize());

        //this.mask = (1 << bits) - 1;
    }

    protected Seq(String header, String[] sequence) {
        if (sequence == null || sequence.length < 1) throw new IllegalArgumentException("Null sequence");

        this.sequence = clean(this, sequence);
        this.length = this.sequence.length();
        this.header = (header != null) ? header : Integer.toString(this.length);
        int i = header.indexOf(' ');
        this.name = nameClean((i > 0) ? header.substring(0, i) : header);
        //this.data = convert(this.sequence);

        //this.bits = getElementSize();
        //this.density = (byte) (WORD_SIZE / bits);
        //if (density < 1) throw new InvalidParameterException("Invalid size: "+getElementSize());

        //this.mask = (1 << bits) - 1;
    }

    //  Getters
    public String getHeader() {
        return header;
    }
    public String getName() {
        int i = header.indexOf(' ');
        return (i > 0) ? nameClean(header.substring(0, i)) : header;
    }
    public String getSequence() {
        return sequence;
    }
    @Override
    public int length() {
        return length;
    }
    @Override
    public char charAt(int index) {
        return (validPosition(index)) ? sequence.charAt(index) : INVALID;
    }
    public int valueAt(int index) {
        return (validPosition(index)) ? charToBinary(sequence.charAt(index)) : -1;
    }
    public int[] values() {
        int[] values = new int[length];
        for (int i = 0; i < length; i++) values[i] = charToBinary(sequence.charAt(i));
        return values;
    }
    public int[] values(int offset) {
        int[] values = new int[length+offset];
        for (int i = 0; i < length; i++) values[i+offset] = charToBinary(sequence.charAt(i));
        return values;
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
    public abstract String getType();
    public abstract char[] getLexicon();
    public abstract String[] getLexiconAsStrings();
    public abstract byte[] getTranslations();
    public abstract byte getElementSize();
    public abstract int charToBinary(char c);

    public boolean sameType(Seq seq) {
        //if (getBits() != seq.getBits()) return false;
        //if (getDensity() != seq.getDensity()) return false;
        //if (getMask() != seq.getMask()) return false;
        return getLexiconAsString().equals(seq.getLexiconAsString());
    }

    //  Should override
    public long getLexiconLength() {
        return getLexicon().length;
    }
    public String getLexiconAsString() {
        char[] lexicon = getLexicon();
        StringBuilder lexiconString = new StringBuilder(lexicon.length);
        for (char c : lexicon) lexiconString.append(Character.toUpperCase(c));
        return lexiconString.toString();
    }

    //  Helpers
    protected static String clean(Seq type, String... sequence) {
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

    protected static String nameClean(String name) {
        if (name == null || name.length() < 1) {
            throw new InvalidParameterException("No name");
        }
        return name.replaceAll("[^A-Za-z0-9_-]", "");
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

    //  Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seq)) return false;

        Seq seq = (Seq) o;

        if (length != seq.length) return false;
        //if (getBits() != seq.getBits()) return false;
        //if (getDensity() != seq.getDensity()) return false;
        //if (getMask() != seq.getMask()) return false;
        if (!getHeader().equals(seq.getHeader())) return false;
        return getSequence().equals(seq.getSequence());
    }

    @Override
    public int hashCode() {
        int result = getHeader().hashCode();
        result = 31 * result + getSequence().hashCode();
        //result = 31 * result + Arrays.hashCode(getData());
        result = 31 * result + length;
        //result = 31 * result + (int) getBits();
        //result = 31 * result + (int) getDensity();
        //result = 31 * result + (int) (getMask() ^ (getMask() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return getType() + '[' + header + ']' + sequence;
    }


    /*public long[] getData() {
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
    }//*/

    /*public long[] convert(String input) {
        return convert(this, input);
    }//*/

    /*
    protected static long[] convert(Seq type, String input) {
        byte bits = type.getElementSize();
        byte density = type.getDensity();
        if (density < 1) throw new InvalidParameterException("Invalid size: "+bits);

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
    }//*/


/*public static long[] convert(sequence.Seq type, String... input) {
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
