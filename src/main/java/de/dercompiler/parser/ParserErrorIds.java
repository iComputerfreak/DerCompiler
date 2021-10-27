package de.dercompiler.parser;

import de.dercompiler.io.message.IErrorIds;

public enum ParserErrorIds implements IErrorIds {
    TODO(300),
    
    ;

    private final int id;

    ParserErrorIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
