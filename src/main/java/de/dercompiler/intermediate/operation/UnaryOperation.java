package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;
import firm.Mode;

public final class UnaryOperation extends Operation {

    private Mode mode;
    private Operand operand;
    private final OperationType operationType;

    public UnaryOperation(OperationType operationType, Operand operand) {
        this.operationType = operationType;
        this.operand = operand;
    }

    @Override
    public Operand[] getArgs() {
        return new Operand[]{operand};
    }

    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String getIntelSyntax() {
        return operationType.getSyntax() + " " + operand.getIdentifier();
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "%s %s (%s)".formatted(operationType, operand, mode);
    }
}
