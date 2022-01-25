package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jae extends UnaryOperation {

    public Jae(LabelOperand address, boolean isMemoryOperation) {
        super(OperationType.JAE, address, isMemoryOperation);
    }

}
