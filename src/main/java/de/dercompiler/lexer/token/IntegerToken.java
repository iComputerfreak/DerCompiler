package de.dercompiler.lexer.token;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.Objects;

/**
 * Represents an Integer literal Token and its literal value.
 * Parsing the value is delegated to later stages where the sign of the integer is clear.
 */
public final class IntegerToken implements IToken {
    private final String value;

    public IntegerToken(String value) {
        if (Objects.isNull(value) || value.length() == 0) {
            new OutputMessageHandler(MessageOrigin.LEXER).internalError("IntegerToken(value) called with empty or null value");
        }
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
