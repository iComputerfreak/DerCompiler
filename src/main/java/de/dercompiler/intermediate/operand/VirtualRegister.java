package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;

public class VirtualRegister implements IRRegister {
    
    private static long nextID = 0;
    
    private final long id;

    public VirtualRegister() {
        this.id = nextID++;
    }
    
    public void resetNextID() {
        nextID = 0;
    }

    public long getId(){
        return id;
    }

    @Override
    public String getIdentifier() {
        return "@V" + id;
    }

    @Override
    public String getIdentifier(Datatype datatype) {
        return getIdentifier();
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
