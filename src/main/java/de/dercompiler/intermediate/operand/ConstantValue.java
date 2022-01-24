package de.dercompiler.intermediate.operand;

import firm.Mode;

public class ConstantValue implements Operand {
    
    private final int value;
    private Mode mode;


    public ConstantValue(int value) {
        this.value = value;
    }
    
    @Override
    public String getIdentifier() {
        return "0x%x".formatted(value);
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
