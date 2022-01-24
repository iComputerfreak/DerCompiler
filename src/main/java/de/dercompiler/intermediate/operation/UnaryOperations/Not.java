package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Not extends UnaryOperation {

    public Not(Operand operand){
        super(OperationType.NOT, operand);
    }

}