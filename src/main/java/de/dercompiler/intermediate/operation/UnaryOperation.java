package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.Operand;

public final class UnaryOperation implements Operation {

    private final OperationType operationType;

    public UnaryOperation(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public Operand[] getArgs() {
        return new Operand[0];
    }

    public OperationType getOperationType() {
        return operationType;
    }
}
