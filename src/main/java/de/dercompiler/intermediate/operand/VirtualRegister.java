package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;

public class VirtualRegister implements IRRegister {
    
    private static int nextID = 0;
    
    private final int id;
    private boolean phiVariable;

    public VirtualRegister() {
        this.id = nextID++;
    }
    
    public void resetNextID() {
        nextID = 0;
    }

    @Override
    public int getId(){
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

    public boolean isPhiVariable() {
        return phiVariable;
    }

    public void setPhiVariable(boolean phiVariable) {
        this.phiVariable = phiVariable;
    }
}
