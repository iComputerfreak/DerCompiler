package de.dercompiler.transformation;

import de.dercompiler.io.message.IWarningIds;

public enum TransformationWarrningIds implements IWarningIds {
    STACK_EMPTY(100),
    OVERRIDE_PREPARED_NODE(1000),
    NULL_REFERENCE(1010);

    private int id;

    TransformationWarrningIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
