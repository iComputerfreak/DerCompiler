package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
/*
Shift left

Das rechte Argument ist entweder die Konstante 1, ein 1-byte immediate value oder das CL Register
 */
public class Shl extends ShiftOperation {

    public Shl(Operand definition, Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.SHL, definition, target, source, isMemoryOperation);
    }

}
