package de.dercompiler.lexer.token;

/**
 * Represents an Integer literal Token and its literal value.
 * Parsing the value is delegated to later stages where the sign of the integer is clear.
 */
public class IntegerToken implements IToken {
    private String value;

    public IntegerToken(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "integer literal %s".formatted(value);
    }

    public String getValue() {
        return value;
    }
}
