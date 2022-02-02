package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

/*
Shift right

Das rechte Argument ist entweder die Konstante 1, ein 1-byte immediate value oder das CL Register
 */
public class Sar extends ShiftOperation {

    public Sar(Operand target, Operand source) {
        super(OperationType.SAR, target, source, false);
    }

    @Override
    public Sar allocate(Operand target, Operand source){
        Sar sar = new Sar(target, source);
        sar.setMode(getMode());
        return sar;
    }
}
