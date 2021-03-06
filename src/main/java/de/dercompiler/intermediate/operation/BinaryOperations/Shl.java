package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
/*
Shift left

Das rechte Argument ist entweder die Konstante 1, ein 1-byte immediate value oder das CL Register
 */
public class Shl extends ShiftOperation {

    public Shl(Operand target, Operand source) {
        super(OperationType.SHL, target, source, false);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        Shl shl = new Shl(target, source);
        shl.setMode(getMode());
        return shl;
    }
}
