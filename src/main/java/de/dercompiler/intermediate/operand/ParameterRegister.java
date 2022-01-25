package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.IRMode;

public class ParameterRegister implements Register {

    private static long nextID = 0;
    private final long id;
    private IRMode mode;

    public ParameterRegister() {
        this.id = nextID++;
    }

    public static void resetNextID() {
        nextID = 0;
    }

    @Override
    public String getIdentifier() {
        return "@P%d".formatted(id);
    }


    @Override
    public String toString() {
        return getIdentifier();
    }
}
