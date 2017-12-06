package alignment;

import sequence.Seq;

public class Alignment {
    private static final boolean DEBUG = false;

    public final static char GAP = '-';

    private       Node start, current, end;
    //private final Node end;
    private final Seq  first, second;
    private final int endIndexFirst, endIndexSecond;
    private int startIndexFirst, startIndexSecond, num;
    private final int score;
    private final boolean isGlobal;

    public Alignment(int score, Seq first, Seq second, boolean isGlobal, int endIndexFirst, int endIndexSecond) { // , char endFirst, char endSecond
        if (DEBUG) System.out.println(((isGlobal) ? "Global" : "Local")+" Alignment at "+endIndexFirst+", "+endIndexFirst+" with score "+score);
        this.score = score;
        this.first = first;
        this.second = second;
        this.isGlobal = isGlobal;
        //  TODO: Validity check on index/isGlobal?
        this.endIndexFirst = endIndexFirst;
        this.endIndexSecond = endIndexSecond;
        this.num = 0;
        this.current = this.end = null; //new Node(endFirst, endSecond);
        this.start = null;
    }

    protected Alignment(Alignment toCopy) {
        this.score = toCopy.score;
        this.first = toCopy.first;
        this.second = toCopy.second;
        this.isGlobal = toCopy.isGlobal;
        this.endIndexFirst = toCopy.endIndexFirst;
        this.endIndexSecond = toCopy.endIndexSecond;
        this.num = toCopy.num;
        if (toCopy.end != null) {
            this.end = new Node(toCopy.end);
            if (toCopy.isFinished()) {
                this.current = null;
                this.start = this.end.copyPath(toCopy.end);
            } else {
                this.current = this.end.copyPath(toCopy.end);
                this.start = null;
            }
        }
        if (DEBUG) System.out.println("Copied "+((isGlobal) ? "Global" : "Local")+" Alignment "+score);
    }

    public Alignment append(char first, char second) {
        if (isFinished()) return this;
        if (this.end == null) {
            this.current = this.end = new Node(first, second);
            if (DEBUG) System.out.println("Starting with "+first+((first == second) ? "-" : " ")+second+" at "+num);
        } else {
            this.current = new Node(first, second, this.current);
            if (DEBUG) System.out.println("Appending "+first+((first == second) ? "-" : " ")+second+" at "+num);
        }
        num++;
        return this;
    }

    public Alignment append(char c, boolean isFirst) {
        return (isFirst) ? append(c, GAP) : append(GAP, c);
    }

    public Alignment appendFirst(char first) {
        return append(first, GAP);
    }

    public Alignment appendSecond(char second) {
        return append(GAP, second);
    }

    public Alignment gap(char c, boolean gapSecond) {
        return (gapSecond) ? append(c, GAP) : append(GAP, c);
    }

    public Alignment gapFirst(char second) {
        return append(GAP, second);
    }

    public Alignment gapSecond(char first) {
        return append(first, GAP);
    }

    public Alignment copy() {
        return new Alignment(this);
    }

    public Alignment fixStart(int startIndexFirst, int startIndexSecond) {
        if (isFinished()) return this;
        //  TODO: Validity check on index/isGlobal?
        this.startIndexFirst = startIndexFirst;
        this.startIndexSecond = startIndexSecond;
        if (this.endIndexFirst == startIndexFirst && this.endIndexSecond == startIndexSecond) { // Obscure trivial case
            this.start = this.end;
            this.end.prev = null;
        } else {
            this.start = current;
        }
        this.current = null; // Mark as finished

        if (DEBUG) System.out.println("Fixing start at "+startIndexFirst+", "+startIndexSecond+" with "+num);

        return this;
    }

    public boolean isFinished() {
        return this.end != null && this.current == null;
    }

    public boolean isStarted() {
        return this.num > 0;
    }

    private class Node {
        private final char first, second;
        private Node next, prev;

        private Node(char first, char second) {
            this.first = first;
            this.second = second;
        }

        private Node(char first, char second, Node next) {
            this(first, second);
            this.next = next;
            this.next.prev = this;
        }

        private Node(Node toCopy) {
            this(toCopy.first, toCopy.second);
        }

        private Node(Node toCopy, Node prev) {
            this(toCopy.first, toCopy.second, prev);
        }

        private boolean contains(char first, char second) {
            return this.first == first && this.second == second;
        }

        private Node copyPath(Node toCopy) {
            if (toCopy == null) return null;
            Node copyNext = toCopy.prev;
            Node current = this;
            while (copyNext != null) {
                current = new Node(copyNext, current);
                copyNext = copyNext.prev;
            }
            return current;
        }
    }

    public char[][] toChars() {
        if (!isFinished()) return null;

        char[][] chars = new char[3][num];

        Node current = this.start;
        int at = 0;

        while (current != null) {
            chars[0][at] = current.first;
            chars[1][at] = (current.first == current.second) ? '|' : ' ';
            chars[2][at] = current.second;

            if (DEBUG) System.out.println("Evaluating col "+at+" as "+chars[0][at]+(" "+chars[1][at]+" ")+chars[2][at]);

            current = current.next;
            at++;
        }

        return chars;
    }

    @Override
    public String toString() {
        if (!isFinished()) return "";

        char[][] chars = toChars();

        StringBuilder sb = new StringBuilder(3*(num+1));

        sb.append(chars[0]).append('\n');
        sb.append(chars[1]).append('\n');
        sb.append(chars[2]);

        return sb.toString();
    }
}
