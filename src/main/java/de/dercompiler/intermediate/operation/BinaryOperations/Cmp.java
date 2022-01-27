package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

public class Cmp extends BinArithOperation {

    public Cmp(Operand definition, Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.CMP, definition, target, source, isMemoryOperation);
    }

}
