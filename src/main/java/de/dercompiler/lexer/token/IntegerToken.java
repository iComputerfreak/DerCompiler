package de.dercompiler.lexer.token;

public class IntegerToken implements IToken {
    private int value;

    public IntegerToken(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "integer literal %d".formatted(value);
    }
}
