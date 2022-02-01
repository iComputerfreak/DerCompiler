package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;

public class ParameterRegister implements IRRegister {

    private final long number;
    public ParameterRegister(int number) {
        this.number = number;
    }

    @Override
    public String getIdentifier() {
        return "@P%d".formatted(number);
    }

    @Override
    public String getIdentifier(Datatype datatype) {
        return getIdentifier();
    }

    public int getId(){
        return (int) number;
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
