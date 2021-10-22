package de.dercompiler.general;

import de.dercompiler.io.message.IErrorIds;

public enum GeneralErrorIds implements IErrorIds {

    FILE_NOT_FOUND(100),

    INVALID_COMMAND_LINE_ARGUMENTS(200),

    ;

    private int id;

    GeneralErrorIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
