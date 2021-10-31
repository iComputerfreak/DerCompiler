package de.dercompiler.lexer.token;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.LexerErrorIds;
import de.dercompiler.lexer.StringTable;

import java.util.Objects;

public final class IdentifierToken implements IToken {
    private String identifier;

    private IdentifierToken(String identifier) {
        this.identifier = identifier;
    }

    public static IdentifierToken forIdentifier(String identifier) {
        if (Objects.isNull(identifier) || identifier.length() == 0) {
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndExit(LexerErrorIds.INVALID_IDENTIFIER, "IdentifierToken(value) called with empty or null value");
        }
        IdentifierToken token;

        StringTable stringTable = StringTable.getInstance();
        if (!stringTable.hasKey(identifier)) {
            token = new IdentifierToken(identifier);
            stringTable.set(identifier, token);
        } else {
            token = stringTable.get(identifier);
        }
        return token;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return "identifier %s".formatted(identifier);
    }
}
