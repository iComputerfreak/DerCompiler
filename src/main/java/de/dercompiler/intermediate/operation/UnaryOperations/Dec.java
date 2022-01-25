package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Dec extends UnaryOperation {

    public Dec(Register register, boolean isMemoryOperation) {
        super(OperationType.DEC, register, isMemoryOperation);
    }

}
