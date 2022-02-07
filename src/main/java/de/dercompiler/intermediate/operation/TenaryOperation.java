package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.generation.AtntTranslator;
import de.dercompiler.intermediate.generation.IntelTranslator;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.TernaryOperations.IMul3;

public abstract non-sealed class TenaryOperation extends Operation {

    protected Operand target;
    protected Operand source;
    protected ConstantValue constant;
    protected OperationType operationType;

    protected TenaryOperation(OperationType type, Operand target, Operand source, ConstantValue constant, boolean isMemoryOperation) {
        super(isMemoryOperation);
        this.operationType = type;
        this.target = target;
        this.source = source;
        this.constant = constant;
    }

    @Override
    public Operand[] getArgs() {
        return new Operand[]{target, source, constant};
    }

    public Operand getTarget() {
        return target;
    }

    public Operand getSource() {
        return source;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String getIntelSyntax() {
        return operationType.getSyntax() + " " + target.acceptTranslator(IntelTranslator.getInstance(), getDatatype()) + ", " + source.acceptTranslator(IntelTranslator.getInstance(), getDatatype()) + ", " + constant.acceptTranslator(IntelTranslator.getInstance(), getDatatype());
    }

    @Override
    public String getAtntSyntax() {
        return operationType.getAtntSyntax(getDatatype()) + " " + constant.acceptTranslator(AtntTranslator.getInstance(), getDatatype()) + "," + source.acceptTranslator(AtntTranslator.getInstance(), getDatatype()) + "," + target.acceptTranslator(AtntTranslator.getInstance(), getDatatype());
    }
}
