package de.dercompiler.pass;

import de.dercompiler.io.message.IErrorIds;

public enum PassErrorIds implements IErrorIds {

    UNKNOWN_EXPRESSION(700),
    TYPE_MISMATCH(701),
    UNDEFINED_VARIABLE(702),
    UNKNOWN_METHOD(703),
    ILLEGAL_METHOD_CALL(704),
    UNKNOWN_FIELD(705),
    ILLEGAL_FIELD_REFERENCE(706),
    ARGUMENTS_MISMATCH(707);

    private int id;

    PassErrorIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
