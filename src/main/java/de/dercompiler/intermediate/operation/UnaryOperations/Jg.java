package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jg extends UnaryOperation {

    public Jg(LabelOperand address, boolean isMemoryOperation) {
        super(OperationType.JG, address, isMemoryOperation);
    }

}
