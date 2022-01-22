package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
/*
Rotate right

Das rechte Argument ist entweder die Konstante 1 oder das CL Register
 */
public class Ror extends BinaryOperation {

    public Ror(Register target, Register source){
        super(OperationType.ROR, target, source);
    }

    public Ror(Register target, ConstantValue source){
        super(OperationType.ROR, target, source);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

    public Ror(Address target, Register source){
        super(OperationType.ROR, target, source);
    }

    public Ror(Address target, ConstantValue source){
        super(OperationType.ROR, target, source);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

}