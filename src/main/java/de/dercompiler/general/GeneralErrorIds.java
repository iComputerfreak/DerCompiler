package de.dercompiler.general;

import de.dercompiler.io.message.IErrorIds;

/**
 * The ErrorIds for general use and no specific location inside the program
 */
public enum GeneralErrorIds implements IErrorIds {
    DO_NOT_USE_RESERVED_FOR_INTERNAL_ERROR(1),

    FILE_NOT_FOUND(100),
    IO_EXCEPTION(110),
    INVALID_WORKING_DIRECTORY(120),

    INVALID_COMMAND_LINE_ARGUMENTS(200),
    TOO_MANY_ACTIONS(210),
    MISSING_INPUT_FILE(220),

    TEST_FAILED(221);

    private final int id;

    GeneralErrorIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
