package de.dercompiler.intermediate.operand;

public class ConstantValue implements Operand {
    
    private final int value;

    public ConstantValue(int value) {
        this.value = value;
    }
    
    @Override
    public String getIdentifier() {
        return "0x%X".formatted(value);
    }

    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return getIdentifier();
    }
}
