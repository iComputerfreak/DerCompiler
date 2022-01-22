package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Or extends BinaryOperation {

    public Or(Register target, Register source){
        super(OperationType.OR, target, source);
    }

    public Or(Register target, Address source){
        super(OperationType.OR, target, source);
    }

    public Or(Address target, Register source){
        super(OperationType.OR, target, source);
    }

    public Or(Register target, ConstantValue source){
        super(OperationType.OR, target, source);
    }

    public Or(Address target, ConstantValue source){
        super(OperationType.OR, target, source);
    }
}