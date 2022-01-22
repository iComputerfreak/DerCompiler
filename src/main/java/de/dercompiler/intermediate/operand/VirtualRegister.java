package de.dercompiler.intermediate.operand;

import firm.Mode;

public class VirtualRegister implements Register {
    
    private static long nextID = 0;
    
    private final long id;
    private Mode mode;

    public VirtualRegister() {
        this.id = nextID++;
    }
    
    public void resetNextID() {
        nextID = 0;
    }

    @Override
    public String getIdentifier() {
        return "@V" + id;
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
