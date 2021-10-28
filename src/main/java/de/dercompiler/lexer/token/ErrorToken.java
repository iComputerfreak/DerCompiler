package de.dercompiler.lexer.token;

import de.dercompiler.io.message.IErrorIds;
import de.dercompiler.lexer.token.IToken;

public final class ErrorToken implements IToken {
    private final IErrorIds error;

    public ErrorToken(IErrorIds id) {
        this.error = id;
    }

    @Override
    public String toString() {
        return error.toString();
    }
}
