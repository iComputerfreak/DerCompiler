package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Movslq extends BinArithOperation{
    public Movslq(Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.MOVSLQ, target, source, isMemoryOperation);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source) {
        return new Movslq(target, source, true);
    }
}
