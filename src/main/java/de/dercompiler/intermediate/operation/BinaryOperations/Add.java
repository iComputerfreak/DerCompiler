package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Add extends BinaryOperation {

    public Add(Register target, Register source){
        super(OperationType.ADD, target, source);
    }

    public Add(Register target, Address source){
        super(OperationType.ADD, target, source);
    }

    public Add(Address target, Register source){
        super(OperationType.ADD, target, source);
    }

    public Add(Register target, ConstantValue source){
        super(OperationType.ADD, target, source);
    }

    public Add(Address target, ConstantValue source){
        super(OperationType.ADD, target, source);
    }
}