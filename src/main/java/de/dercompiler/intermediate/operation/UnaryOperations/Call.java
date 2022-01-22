package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Call extends UnaryOperation {

    public Call(LabelOperand address){
        super(OperationType.CALL, address);
    }

}