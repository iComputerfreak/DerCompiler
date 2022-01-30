package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class And extends BinaryOperation {

    public And(Operand target, Operand source){
        super(OperationType.AND, target, source, false);
    }


    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        return new And(target, source);
    }
}
