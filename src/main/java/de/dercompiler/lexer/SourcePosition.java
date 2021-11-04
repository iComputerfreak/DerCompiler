package de.dercompiler.lexer;

public class SourcePosition {

    protected int line;
    protected int column;

    SourcePosition() {
        // after reading first character, position is at 1:1 and from then on it is correct
        this.line = 1;
        this.column = 0;
    }

    SourcePosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "%d:%d".formatted(this.line, this.column);
    }

    public SourcePosition copy() {
        return new SourcePosition(this.line, this.column);
    }
}
