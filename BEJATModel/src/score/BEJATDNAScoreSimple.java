package score;

public class BEJATDNAScoreSimple extends BEJATDNAScore {
    private final int equal;
    private final int unequal;
    private final int gap;

    public BEJATDNAScoreSimple(int equal, int unequal, int gap) {
        this.equal = equal;
        this.unequal = unequal;
        this.gap = gap;
    }

    @Override
    public int s(char a, char b) {
        return (a == b) ? equal : unequal;
    }

    @Override
    public int s(byte a, byte b) {
        return (a == b) ? equal : unequal;
    }

    @Override
    public int w(int l) {
        return gap;
    }
}
