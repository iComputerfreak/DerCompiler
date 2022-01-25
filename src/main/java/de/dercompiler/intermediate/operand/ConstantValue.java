package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.Signedness;

public class ConstantValue implements Operand {
    
    private final int value;

    public ConstantValue(int value) {
        this.value = value;
    }
    
    @Override
    public String getIdentifier() {
        return "0x%x".formatted(value);
    }

    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return getIdentifier();
    }
}
