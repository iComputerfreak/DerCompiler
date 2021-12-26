package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.Operand;

public final class UnaryOperation implements Operation {

    private Operand operand;
    private final OperationType operationType;

    public UnaryOperation(OperationType operationType) {
        this.operationType = operationType;
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
}
