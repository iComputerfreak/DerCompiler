package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
/*
Shift right

Das rechte Argument ist entweder die Konstante 1 oder das CL Register
 */
public class Shr extends BinaryOperation {

    public Shr(Register target, Register source){
        super(OperationType.SHR, target, source);
    }

    public Shr(Register target, ConstantValue source){
        super(OperationType.SHR, target, source);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

    public Shr(Address target, Register source){
        super(OperationType.SHR, target, source);
    }

    public Shr(Address target, ConstantValue source){
        super(OperationType.SHR, target, source);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

}