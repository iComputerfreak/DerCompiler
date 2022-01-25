package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class And extends BinaryOperation {

    public And(Operand target, Operand source, boolean isMemoryOperation){
        super(OperationType.AND, target, source, isMemoryOperation);
    }

}
