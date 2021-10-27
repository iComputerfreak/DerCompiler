package de.dercompiler.lexer.token;

import de.dercompiler.lexer.StringTable;

public class IdentifierToken implements IToken {
    private String identifier;

    private IdentifierToken(String identifier) {
        this.identifier = identifier;
    }

    public static IdentifierToken forIdentifier(String identifier) {
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

    @Override
    public String toString() {
        return "identifier %s".formatted(identifier);
    }
}
