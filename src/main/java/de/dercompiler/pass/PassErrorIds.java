package de.dercompiler.pass;

import de.dercompiler.io.message.IErrorIds;

public enum PassErrorIds implements IErrorIds {

    UNKNOWN_EXPRESSION(700);

    private int id;

    PassErrorIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
