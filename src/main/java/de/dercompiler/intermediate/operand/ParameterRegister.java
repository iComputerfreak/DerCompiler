package de.dercompiler.intermediate.operand;

import firm.Mode;

public class ParameterRegister implements Register {

    private static long nextID = 0;
    private long id;
    private Mode mode;

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
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
