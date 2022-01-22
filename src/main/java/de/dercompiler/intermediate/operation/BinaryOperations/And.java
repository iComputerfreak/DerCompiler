package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class And extends BinaryOperation {

    public And(Register target, Register source){
        super(OperationType.AND, target, source);
    }

    public And(Register target, Address source){
        super(OperationType.AND, target, source);
    }

    public And(Address target, Register source){
        super(OperationType.AND, target, source);
    }

    public And(Register target, ConstantValue source){
        super(OperationType.AND, target, source);
    }

    public And(Address target, ConstantValue source){
        super(OperationType.AND, target, source);
    }
}