package de.dercompiler.intermediate.operation2.Operand;

public class VirtualRegister extends Register{

    private static long nextID = 0;

    private final long id;

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
}
