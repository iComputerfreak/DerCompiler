package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Xor extends BinaryOperation {

    public Xor(Register target, Register source){
        super(OperationType.XOR, target, source);
    }

    public Xor(Register target, Address source){
        super(OperationType.XOR, target, source);
    }

    public Xor(Address target, Register source){
        super(OperationType.XOR, target, source);
    }

    public Xor(Register target, ConstantValue source){
        super(OperationType.XOR, target, source);
    }

    public Xor(Address target, ConstantValue source){
        super(OperationType.XOR, target, source);
    }
}