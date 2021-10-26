package de.dercompiler.lexer;

import de.dercompiler.io.message.IErrorIds;
import de.dercompiler.io.message.IWarningIds;

public enum LexerErrorIds implements IErrorIds {
    BUFFER_UNDERFLOW(200),
    BUFFER_OVERFLOW(201),
    BUFFER_TOO_SMALL(202),
    BUFFER_TOO_MUCH_LOOKAHEAD(203),
    UNKNOWN_SYMBOL(204),
    INVALID_INTEGER_LITERAL(205);

    private int id;

    LexerErrorIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
