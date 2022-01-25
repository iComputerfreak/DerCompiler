package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

public class Or extends BinArithOperation {

    public Or(Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.OR, target, source, isMemoryOperation);
    }

}
