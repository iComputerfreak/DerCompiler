package de.dercompiler.generation;

import de.dercompiler.io.message.IWarningIds;

public enum CodeGenerationWarningIds implements IWarningIds {
    UNKNOWN_TOOL(400),
    ;

    private int id;

    CodeGenerationWarningIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
