package de.dercompiler.pass;

import de.dercompiler.io.message.IWarningIds;

public enum PassWarningIds implements IWarningIds {

    NULL_AS_PASS_NOT_ALLOWED(100),


    ;

    private int id = 0;

    PassWarningIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return 0;
    }
}
