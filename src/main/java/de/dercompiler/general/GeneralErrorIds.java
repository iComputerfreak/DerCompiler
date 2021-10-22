package de.dercompiler.general;

import de.dercompiler.io.message.IErrorIds;

public enum GeneralErrorIds implements IErrorIds {

    FILE_NOT_FOUND(100),
    IO_EXCEPTION(110),

    INVALID_COMMAND_LINE_ARGUMENTS(200),
    TO_MANY_ACTIONS(210),
    MISSING_INPUT_FILE(220),

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
