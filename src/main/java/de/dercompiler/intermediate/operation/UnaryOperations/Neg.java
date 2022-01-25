package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Neg extends UnaryOperation {
    public Neg(Operand target, boolean isMemoryOperation) {
        super(OperationType.NEG, target, isMemoryOperation);
    }
}
