package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jbe extends UnaryOperation {

    public Jbe(LabelOperand address, boolean isMemoryOperation) {
        super(OperationType.JBE, address, isMemoryOperation);
    }

}
