package score;

import sequence.NSeq;

public abstract class NScorer extends Scorer {
    public static final byte SIZE = NSeq.ELEMENT_SIZE;
    public static final byte N = NSeq.LEXICON_LENGTH;
}
