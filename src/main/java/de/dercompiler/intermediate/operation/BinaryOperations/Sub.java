package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

public class Sub extends BinArithOperation {

    public Sub(Operand definition, Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.SUB, definition, target, source, isMemoryOperation);
    }

}
