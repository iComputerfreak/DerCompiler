package de.dercompiler.intermediate.operand;

import firm.Mode;

public class VirtualRegister implements Operand {
    
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
        return "@" + id;
    }

    @Override
    public Mode getMode() {
        return mode;
    }
}
