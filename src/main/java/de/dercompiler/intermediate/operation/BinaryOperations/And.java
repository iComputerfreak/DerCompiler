package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class And extends BinaryOperation {

    public And(Operand target, Operand source){
        super(OperationType.AND, target, source);
    }

}