package de.dercompiler.intermediate;

import de.dercompiler.io.message.IWarningIds;

public enum CodeGenerationWarningIds implements IWarningIds {
    UNKNOWN_TOOL(400),
    MISSING_RULE(401),
    NOT_ALL_REGISTERS_USED_IN_CALLING_CONVENTION(500),
    BLOCK_ORDER_FAIL(600);

    private final int id;

    CodeGenerationWarningIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
