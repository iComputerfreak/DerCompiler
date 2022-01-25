package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public final class Cwtl extends UnaryOperation {

    public Cwtl(Operand operand, boolean isMemoryOperation) {
        super(OperationType.CWTL, operand, isMemoryOperation);
    }
}
