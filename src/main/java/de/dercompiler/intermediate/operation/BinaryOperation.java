package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;

import firm.Mode;

public final class BinaryOperation extends Operation {

    private Operand[] operands;
    private final OperationType operationType;
    private Mode mode;

    public BinaryOperation(OperationType operationType, Operand op1, Operand op2) {
        this.operationType = operationType;
        this.operands = new Operand[] {op1, op2};
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

    @Override
    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
