package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Cmp extends BinaryOperation {

    public Cmp(Register target, Register source){
        super(OperationType.CMP, target, source);
    }

    public Cmp(Register target, Address source){
        super(OperationType.CMP, target, source);
    }

    public Cmp(Address target, Register source){
        super(OperationType.CMP, target, source);
    }

    public Cmp(Register target, ConstantValue source){
        super(OperationType.CMP, target, source);
    }

    public Cmp(Address target, ConstantValue source){
        super(OperationType.CMP, target, source);
    }
}