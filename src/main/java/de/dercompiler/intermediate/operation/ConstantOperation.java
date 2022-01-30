package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.selection.Datatype;

public abstract non-sealed class ConstantOperation extends Operation {


    private final OperationType operationType;

    public ConstantOperation(OperationType operationType, boolean isMemoryOperation) {
        super(isMemoryOperation);
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
    public String getAtntSyntax(Datatype datatype) {
        return operationType.getAtntSyntax(datatype);
    }

}
