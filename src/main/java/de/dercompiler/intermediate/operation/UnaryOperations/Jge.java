package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jge extends UnaryOperation {

    public Jge(LabelOperand address, boolean isMemoryOperation) {
        super(OperationType.JGE, address, isMemoryOperation);
    }

}
