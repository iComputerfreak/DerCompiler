package de.dercompiler.general;

import de.dercompiler.io.message.IWarningIds;

public enum GeneralWarningIds implements IWarningIds {

    INVALID_COMMAND_LINE_ARGUMENTS(200),

    ;

    private int id;

    GeneralWarningIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
