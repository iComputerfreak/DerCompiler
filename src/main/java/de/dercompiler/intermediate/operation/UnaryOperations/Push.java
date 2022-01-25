package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Push extends UnaryOperation {

    public Push(Register register, boolean isMemoryOperation) {
        super(OperationType.DEC, register, isMemoryOperation);
    }

}
