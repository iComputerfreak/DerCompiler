package de.dercompiler.lexer;

import de.dercompiler.lexer.token.IdentifierToken;

import java.util.HashMap;
import java.util.Map;

public class StringTable {

    private Map<String, IdentifierToken> map;

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
