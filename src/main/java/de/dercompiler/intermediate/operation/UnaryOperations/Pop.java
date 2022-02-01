package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Pop extends UnaryOperation {

    public Pop(Register register, boolean isMemoryOperation) {
        super(OperationType.POP, register, isMemoryOperation);
    }

}