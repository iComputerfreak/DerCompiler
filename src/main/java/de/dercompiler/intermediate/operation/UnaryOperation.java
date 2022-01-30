package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;

import java.util.Objects;

public abstract non-sealed class UnaryOperation extends Operation {

    protected final Operand operand;
    protected final OperationType operationType;

    public UnaryOperation(OperationType operationType, Operand operand, boolean isMemoryOperation) {
        super(isMemoryOperation);
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

}
