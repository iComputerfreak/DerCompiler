package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Mov extends BinaryOperation {

    public Mov(Register target, Register source){
        super(OperationType.MOV, target, source);
    }

    public Mov(Register target, Address source){
        super(OperationType.MOV, target, source);
    }

    public Mov(Address target, Register source){
        super(OperationType.MOV, target, source);
    }

    public Mov(Register target, ConstantValue source){
        super(OperationType.MOV, target, source);
    }

    public Mov(Address target, ConstantValue source){
        super(OperationType.MOV, target, source);
    }
}