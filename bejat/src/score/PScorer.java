package score;

import sequence.PSeq;
import sequence.Seq;

public abstract class PScorer extends Scorer {
    public static final byte SIZE = PSeq.ELEMENT_SIZE;
    public static final byte N = PSeq.LEXICON_LENGTH;
    public int s(char a, char b) {
        return s(PSeq.interpret(a), PSeq.interpret(b));
    }
    public String getType() {
        return PSeq.TYPE;
    }
    public boolean sameType(Seq seq) {
        return seq.getLexiconAsString().equals(PSeq.LEXICON_STRING);
    }
}
