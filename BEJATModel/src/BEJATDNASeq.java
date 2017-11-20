public class BEJATDNASeq extends BEJATNSeq {

    private static final char[] LEXICON = {'G', 'C', 'A', 'T'};
    private static final String[] LEXICON_S = {"G", "C", "A", "T"};

    public char[] getLexicon() {
        return LEXICON;
    }
    public String[] getLexiconAsString() {
        return LEXICON_S;
    }

}
