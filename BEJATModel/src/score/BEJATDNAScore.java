package score;

import sequence.BEJATDNASeq;

public abstract class BEJATDNAScore extends BEJATScore {
    protected static final byte N = 4;
    public int s(char a, char b) {
        return s(BEJATDNASeq.interpret(a), BEJATDNASeq.interpret(b));
    }
}
