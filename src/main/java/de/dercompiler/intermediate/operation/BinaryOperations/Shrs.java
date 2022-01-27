package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

/*
Shift right

Das rechte Argument ist entweder die Konstante 1, ein 1-byte immediate value oder das CL Register
 */
public class Shrs extends BinaryOperation {

    public Shrs(Operand definition, Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.SHRS, definition, target, source, isMemoryOperation);
    }

}
