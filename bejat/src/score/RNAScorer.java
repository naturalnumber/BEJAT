package score;

import sequence.RNASeq;
import sequence.Seq;

public abstract class RNAScorer extends NScorer {
    public int s(char a, char b) {
        return s(RNASeq.interpret(a), RNASeq.interpret(b));
    }
    public String getType() {
        return RNASeq.TYPE;
    }
    public boolean sameType(Seq seq) {
        return seq.getLexiconAsString().equals(RNASeq.LEXICON_STRING);
    }
}
