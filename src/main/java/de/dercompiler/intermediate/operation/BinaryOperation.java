package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;


public abstract non-sealed class BinaryOperation extends Operation {

    private final Operand target;
    private final Operand source;

    private final OperationType operationType;

    public BinaryOperation(OperationType operationType, Operand target, Operand source, boolean isMemoryOperation) {
        super(isMemoryOperation);
        this.operationType = operationType;
        this.target = target;
        this.source = source;
    }

    @Override
    public Operand[] getArgs() {
        return new Operand[]{target, source};
    }

    public Operand getTarget() {
        return target;
    }

    public Operand getSource() {
        return source;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String getIntelSyntax() {
        return operationType.getSyntax() + " " + target.getIdentifier() + " " + source.getIdentifier();
    }

    @Override
    public String toString() {
        return "%s %s %s (%s%s)".formatted(operationType, target, source, mode, isMemoryOperation()? "/M" : "");
    }
}
