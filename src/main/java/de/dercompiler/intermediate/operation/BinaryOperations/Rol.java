package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
/*
Rotate left

Das rechte Argument ist entweder die Konstante 1 oder das CL Register
 */
public class Rol extends ShiftOperation {

    public Rol(Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.ROL, target, source, isMemoryOperation);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        Rol rol = new Rol(target, source, true);
        rol.setMode(getMode());
        return rol;
    }
}
