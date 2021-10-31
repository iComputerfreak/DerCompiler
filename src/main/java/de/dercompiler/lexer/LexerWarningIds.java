package de.dercompiler.lexer;

import de.dercompiler.io.message.IWarningIds;

public enum LexerWarningIds implements IWarningIds {
    NESTED_COMMENT(210);


    private int id;

    LexerWarningIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
