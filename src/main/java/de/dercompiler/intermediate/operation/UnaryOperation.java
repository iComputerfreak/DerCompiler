package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;
import firm.Mode;

import java.util.Objects;

public abstract non-sealed class UnaryOperation extends Operation {

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
        return operationType.getSyntax() + (Objects.isNull(operand) ? "" :  " " + operand.getIdentifier());
    }

    @Override
    public String toString() {
        return "%s %s (%s)".formatted(operationType, operand, mode);
    }
}
