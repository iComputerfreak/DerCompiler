package de.dercompiler.general;

import de.dercompiler.io.message.IWarningIds;

public enum GeneralWarningIds implements IWarningIds {

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
