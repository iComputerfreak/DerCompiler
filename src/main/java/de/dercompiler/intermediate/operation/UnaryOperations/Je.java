package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Je extends UnaryOperation {

    public Je(LabelOperand address, boolean isMemoryOperation) {
        super(OperationType.JE, address, isMemoryOperation);
    }

}
