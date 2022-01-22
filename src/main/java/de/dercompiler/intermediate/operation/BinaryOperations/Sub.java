package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Sub extends BinaryOperation {

    public Sub(Register target, Register source){
        super(OperationType.SUB, target, source);
    }

    public Sub(Register target, Address source){
        super(OperationType.SUB, target, source);
    }

    public Sub(Address target, Register source){
        super(OperationType.SUB, target, source);
    }

    public Sub(Register target, ConstantValue source){
        super(OperationType.SUB, target, source);
    }

    public Sub(Address target, ConstantValue source){
        super(OperationType.SUB, target, source);
    }
}