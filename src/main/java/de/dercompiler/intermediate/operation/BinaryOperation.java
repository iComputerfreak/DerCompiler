package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.Operand;

public final class BinaryOperation implements Operation {

    private Operand[] operands;
    private final OperationType operationType;

    public BinaryOperation(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public Operand[] getArgs() {
        return operands;
    }
    
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String getIntelSyntax() {
        return operationType.getSyntax() + " " + operands[0].getIdentifier() + " " + operands[1].getIdentifier();
    }
}
