package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
/*
Shift left

Das rechte Argument ist entweder die Konstante 1 oder das CL Register
 */
public class Shl extends BinaryOperation {

    public Shl(Register target, Register source){
        super(OperationType.SHL, target, source);
    }

    public Shl(Register target, ConstantValue source){
        super(OperationType.SHL, target, source);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

    public Shl(Address target, Register source){
        super(OperationType.SHL, target, source);
    }

    public Shl(Address target, ConstantValue source){
        super(OperationType.SHL, target, source);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

}