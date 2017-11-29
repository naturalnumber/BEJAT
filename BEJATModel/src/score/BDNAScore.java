package score;

import sequence.BDNASeq;
import sequence.BSeq;

public abstract class BDNAScore extends BNScore {
    public int s(char a, char b) {
        return s(BDNASeq.interpret(a), BDNASeq.interpret(b));
    }
    public String getType() {
        return BDNASeq.TYPE;
    }
    public boolean sameType(BSeq bSeq) {
        return bSeq.getLexiconAsString().equals(BDNASeq.LEXICON_STRING);
    }
}
