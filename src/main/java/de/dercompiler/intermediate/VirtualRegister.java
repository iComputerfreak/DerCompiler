package de.dercompiler.intermediate;

public class VirtualRegister implements Operand {
    
    private static long nextID = 0;
    
    private final long id;

    public VirtualRegister() {
        this.id = nextID++;
    }

    @Override
    public String getIdentifier() {
        return "@" + id;
    }
}
