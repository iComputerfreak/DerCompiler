package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

public class Add extends BinArithOperation {

    public Add(Operand definition, Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.ADD,definition, target, source, isMemoryOperation);
    }

}
