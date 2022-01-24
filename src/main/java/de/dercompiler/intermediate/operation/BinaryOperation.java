package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;

import firm.Mode;

public abstract non-sealed class BinaryOperation extends Operation {

    private final Operand target;
    private final Operand source;

    private final OperationType operationType;
    private Mode mode;

    public BinaryOperation(OperationType operationType, Operand target, Operand source) {
        this.operationType = operationType;
        this.target = target;
        this.source = source;
    }

    @Override
    public Operand[] getArgs() {
        return new Operand[]{target, source};
    }
    
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String getIntelSyntax() {
        return operationType.getSyntax() + " " + target.getIdentifier() + " " + source.getIdentifier();
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "%s %s %s (%s)".formatted(operationType, target, source, mode);
    }
}
