package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.generation.AtntTranslator;
import de.dercompiler.intermediate.generation.IntelTranslator;
import de.dercompiler.intermediate.operand.Operand;


public abstract non-sealed class BinaryOperation extends Operation {

    protected final Operand target;
    protected final Operand source;

    protected final OperationType operationType;

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
        return operationType.getSyntax() + " " + target.acceptTranslator(IntelTranslator.getInstance(), getDatatype()) + ", " + source.acceptTranslator(IntelTranslator.getInstance(), getDatatype());
    }

    @Override
    public String getAtntSyntax(){
        return operationType.getAtntSyntax(getDatatype()) +  " " + source.acceptTranslator(AtntTranslator.getInstance(), getDatatype()) + "," + target.acceptTranslator(AtntTranslator.getInstance(), getDatatype());
    }

    public abstract BinaryOperation allocate(Operand target, Operand source);

}
