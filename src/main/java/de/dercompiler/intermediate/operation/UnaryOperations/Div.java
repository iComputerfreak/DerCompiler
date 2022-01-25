package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Div extends UnaryOperation {

    public Div(Register register, boolean isMemoryOperation) {
        super(OperationType.DIV, register, isMemoryOperation);
    }

}
