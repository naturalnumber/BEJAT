package alignment;

import sequence.Seq;

public class Alignment {
    public final static char GAP = ' ';

    private       Node start, current;
    private final Node end;
    private final Seq  first, second;
    private final int endIndexFirst, endIndexSecond;
    private int startIndexFirst, startIndexSecond;
    private final int score;
    private final boolean isGlobal;

    public Alignment(int score, Seq first, Seq second, boolean isGlobal, int endIndexFirst, int endIndexSecond, char endFirst, char endSecond) {
        this.score = score;
        this.first = first;
        this.second = second;
        this.isGlobal = isGlobal;
        //  TODO: Validity check on index/isGlobal?
        this.endIndexFirst = endIndexFirst;
        this.endIndexSecond = endIndexSecond;
        this.current = this.end = new Node(endFirst, endSecond);
        this.start = null;
    }

    protected Alignment(Alignment toCopy) {
        this.score = toCopy.score;
        this.first = toCopy.first;
        this.second = toCopy.second;
        this.isGlobal = toCopy.isGlobal;
        this.endIndexFirst = toCopy.endIndexFirst;
        this.endIndexSecond = toCopy.endIndexSecond;
        this.end = new Node(toCopy.end);
        if (toCopy.isFinished()) {
            this.current = null;
            this.start = this.end.copyPath(toCopy.end);
        } else {
            this.current = this.end.copyPath(toCopy.end);
            this.start = null;
        }
    }

    public Alignment append(char first, char second) {
        if (isFinished()) return this;
        this.current = new Node(first, second, this.current);
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

    public Alignment fixStart(char first, char second, int startIndexFirst, int startIndexSecond) {
        if (isFinished()) return this;
        //  TODO: Validity check on index/isGlobal?
        this.startIndexFirst = startIndexFirst;
        this.startIndexSecond = startIndexSecond;
        if (this.endIndexFirst == startIndexFirst && this.endIndexSecond == startIndexSecond &&
            this.end.contains(first, second)) { // Obscure trivial case
            this.start = this.end;
        } else {
            this.start = new Node(first, second, this.current);
        }
        this.current = null; // Mark as finished
        return this;
    }

    public Alignment fixStart(char c, boolean isFirst, int startIndexFirst, int startIndexSecond) {
        return (isFirst) ?
               fixStart(c, GAP, startIndexFirst, startIndexSecond) :
               fixStart(GAP, c, startIndexFirst, startIndexSecond);
    }

    public boolean isFinished() {
        return this.current == null;
    }

    private class Node {
        private final char first, second;
        private Node next, prev;

        private Node(char first, char second) {
            this.first = first;
            this.second = second;
        }

        private Node(char first, char second, Node prev) {
            this(first, second);
            this.prev = prev;
            this.prev.next = this;
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
            Node copyNext = toCopy.next;
            Node current = this;
            while (copyNext != null) {
                current = new Node(copyNext, current);
                copyNext = copyNext.next;
            }
            return current;
        }
    }
}
