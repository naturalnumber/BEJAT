package score;

import sequence.DNASeq;
import sequence.Seq;

public abstract class DNAScorer extends NScorer {
    public int s(char a, char b) {
        return s(DNASeq.interpret(a), DNASeq.interpret(b));
    }
    public String getType() {
        return DNASeq.TYPE;
    }
    public boolean sameType(Seq seq) {
        return seq.getLexiconAsString().equals(DNASeq.LEXICON_STRING);
    }
}
