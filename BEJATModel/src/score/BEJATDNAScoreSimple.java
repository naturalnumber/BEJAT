package score;

public class BEJATDNAScoreSimple extends BEJATDNAScore {
    @Override
    public int s(byte a, byte b) {
        return (a == b) ? 5 : -3;
    }

    @Override
    public int w(int l) {
        return -4;
    }
}
