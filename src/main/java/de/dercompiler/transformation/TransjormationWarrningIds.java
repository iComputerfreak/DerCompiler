package de.dercompiler.transformation;

import de.dercompiler.io.message.IWarningIds;

public enum TransjormationWarrningIds implements IWarningIds {
    STACK_EMPTY(100),

    ;

    private int id;

    TransjormationWarrningIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
