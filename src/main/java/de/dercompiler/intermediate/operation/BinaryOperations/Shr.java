package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
/*
Shift right

Das rechte Argument ist entweder die Konstante 1, ein 1-byte immediate value oder das CL Register
 */
public class Shr extends ShiftOperation {

    public Shr(Operand target, Operand source) {
        super(OperationType.SHR, target, source, false);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        Shr shr = new Shr(target, source);
        shr.setMode(getMode());
        return shr;
    }
}
