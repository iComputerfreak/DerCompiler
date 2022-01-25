package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
/*
Rotate left

Das rechte Argument ist entweder die Konstante 1 oder das CL Register
 */
public class Rol extends BinaryOperation {

    public Rol(Register target, Register source, boolean isMemoryOperation) {
        super(OperationType.ROL, target, source, isMemoryOperation);
    }

    public Rol(Register target, ConstantValue source, boolean isMemoryOperation) {
        super(OperationType.ROL, target, source, isMemoryOperation);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

    public Rol(Address target, Register source, boolean isMemoryOperation){
        super(OperationType.ROL, target, source, isMemoryOperation);
    }

    public Rol(Address target, ConstantValue source, boolean isMemoryOperation) {
        super(OperationType.ROL, target, source, isMemoryOperation);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

}
