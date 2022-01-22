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

    public Rol(Register target, Register source){
        super(OperationType.ROL, target, source);
    }

    public Rol(Register target, ConstantValue source){
        super(OperationType.ROL, target, source);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

    public Rol(Address target, Register source){
        super(OperationType.ROL, target, source);
    }

    public Rol(Address target, ConstantValue source){
        super(OperationType.ROL, target, source);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

}