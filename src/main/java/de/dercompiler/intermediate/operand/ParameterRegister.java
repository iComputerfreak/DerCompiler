package de.dercompiler.intermediate.operand;

public class ParameterRegister implements Register {

    private final long number;
    public ParameterRegister(int number) {
        this.number = number;
    }

    @Override
    public String getIdentifier() {
        return "@P%d".formatted(number);
    }


    @Override
    public String toString() {
        return getIdentifier();
    }
}
