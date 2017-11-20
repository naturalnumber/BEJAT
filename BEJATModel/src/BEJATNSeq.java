public abstract class BEJATNSeq extends BEJATSeq {

    public static final byte ELEMENT_SIZE = 2;
    private static final byte[] TRANSLATION = {0, 1, 2, 3};

    public byte[] getTranslations() {
        return TRANSLATION;
    }
    public byte getElementSize() {
        return ELEMENT_SIZE;
    }
}
