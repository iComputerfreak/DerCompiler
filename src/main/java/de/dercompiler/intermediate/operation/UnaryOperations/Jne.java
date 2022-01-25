package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jne extends UnaryOperation {

    public Jne(LabelOperand address, boolean isMemoryOperation) {
        super(OperationType.JNE, address, isMemoryOperation);
    }

}
