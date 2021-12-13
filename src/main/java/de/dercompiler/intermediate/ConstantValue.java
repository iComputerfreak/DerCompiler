package de.dercompiler.intermediate;

public class ConstantValue implements Operand {
    
    private final int value;

    public ConstantValue(int value) {
        this.value = value;
    }
    
    @Override
    public String getIdentifier() {
        return Integer.toString(value);
    }
}
