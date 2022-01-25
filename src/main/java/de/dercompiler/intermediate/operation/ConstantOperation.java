package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;
import firm.Mode;

public abstract non-sealed class ConstantOperation extends Operation {


    private final OperationType operationType;

    public ConstantOperation(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public Operand[] getArgs() {
        return new Operand[0];
    }

    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String getIntelSyntax() {
        return operationType.getSyntax();
    }


    @Override
    public String toString() {
        return "%s (%s)".formatted(operationType, mode);
    }
}
