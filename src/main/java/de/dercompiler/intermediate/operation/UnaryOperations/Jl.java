package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jl extends UnaryOperation {

    public Jl(LabelOperand address, boolean isMemoryOperation) {
        super(OperationType.JL, address, isMemoryOperation);
    }

}
