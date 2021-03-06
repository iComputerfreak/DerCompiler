package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.generation.AtntTranslator;
import de.dercompiler.intermediate.generation.IntelTranslator;
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

    public Operand getTarget() {
        return operand;
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
        return operationType.getSyntax() + (Objects.isNull(operand) ? "" :  " " + operand.acceptTranslator(IntelTranslator.getInstance(), getDatatype()));
    }

    @Override
    public String getAtntSyntax() {
        return operationType.getAtntSyntax(getDatatype()) + (Objects.isNull(getDatatype()) ? "" :  " " + operand.acceptTranslator(AtntTranslator.getInstance(), getDatatype()));
    }

    public Operand getArg() {
        return operand;
    }
}
