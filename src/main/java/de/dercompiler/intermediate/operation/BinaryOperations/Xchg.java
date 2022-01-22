package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Xchg extends BinaryOperation {

    public Xchg(Register target, Register source){
        super(OperationType.XCHG, target, source);
    }

    public Xchg(Register target, Address source){
        super(OperationType.XCHG, target, source);
    }

    public Xchg(Address target, Register source){
        super(OperationType.XCHG, target, source);
    }
}