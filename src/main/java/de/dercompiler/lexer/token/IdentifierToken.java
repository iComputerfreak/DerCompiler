package de.dercompiler.lexer.token;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.LexerErrorIds;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class IdentifierToken implements IToken {
    private final String identifier;

    private IdentifierToken(String identifier) {
        this.identifier = identifier;
    }

    public static IdentifierToken forIdentifier(String identifier) {
        if (Objects.isNull(identifier) || identifier.length() == 0) {
            new OutputMessageHandler(MessageOrigin.LEXER).printErrorAndExit(LexerErrorIds.INVALID_IDENTIFIER, "IdentifierToken(value) called with empty or null value");
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

    public static IdentifierToken proto() {
        return IdentifierToken.forIdentifier("proto");
    }

    public static IdentifierToken error() {
        return IdentifierToken.forIdentifier("<error>");
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return "identifier %s".formatted(identifier);
    }

    private static class StringTable {

        private final Map<String, IdentifierToken> map;

        private static StringTable singleton;

        private StringTable() {
            this.map = new HashMap<>();
        }

        public static StringTable getInstance() {
            if (singleton == null) {
                singleton = new StringTable();
            }
            return singleton;
        }

        public IdentifierToken get(String identifier) {
            return this.map.get(identifier);
        }

        public void set(String identifier, IdentifierToken token) {
            identifier = identifier.intern();
            this.map.put(identifier, token);
        }

        public boolean hasKey(String identifier) {
            return this.map.containsKey(identifier);
        }
    }
}
