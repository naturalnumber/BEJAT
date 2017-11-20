import java.security.InvalidParameterException;

public abstract class BEJATSeq {

    public static final byte WORD_SIZE = 64;


    public abstract char[] getLexicon();
    public abstract String[] getLexiconAsString();
    public abstract byte[] getTranslations();
    public abstract byte getElementSize();

    public static long[] convert(byte size, char[] lexicon, byte[] translation, String... input) {
        byte density = (byte) (WORD_SIZE / size);
        if (density < 1) throw new InvalidParameterException("Invalid size: "+size);

        byte mask = 1;

        for (int i = 1; i < size; i++) mask = (byte) (mask << 2 + 1); //TODO: Check this

        int n = 0;

        for (String s : input) n += s.length();

        int l = n/density + ((n%density > 0) ? 1 : 0);

        long[] output = new long[l];



        return output;
    }

}
