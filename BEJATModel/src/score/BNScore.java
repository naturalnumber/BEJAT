package score;

import sequence.BDNASeq;
import sequence.BNSeq;

public abstract class BNScore extends BScore {
    public static final byte SIZE = BNSeq.ELEMENT_SIZE;
    public static final byte N = BNSeq.LEXICON_LENGTH;
}
