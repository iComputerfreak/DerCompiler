package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Not extends UnaryOperation {

    public Not(Operand operand, boolean isMemoryOperation) {
        super(OperationType.NOT, operand, isMemoryOperation);
    }

}
