package de.dercompiler.lexer.token;

import de.dercompiler.io.message.IErrorIds;
import de.dercompiler.lexer.token.IToken;

public class ErrorToken implements IToken {
    private final IErrorIds error;

    public ErrorToken(IErrorIds id) {
        this.error = id;
    }
}
