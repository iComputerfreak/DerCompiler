package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

public class Cmp extends BinArithOperation {

    public Cmp(Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.CMP, target, source, isMemoryOperation);
    }

}
