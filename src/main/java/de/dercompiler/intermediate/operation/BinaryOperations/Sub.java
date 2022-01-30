package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Sub extends BinArithOperation {

    public Sub(Operand target, Operand source) {
        super(OperationType.SUB, target, source, false);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        return new Sub(target, source);
    }
}
