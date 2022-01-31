package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Add extends BinArithOperation {

    public Add(Operand target, Operand source) {
        super(OperationType.ADD, target, source, false);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source) {
        Add add = new Add(target, source);
        add.setMode(getMode());
        return add;
    }
}
