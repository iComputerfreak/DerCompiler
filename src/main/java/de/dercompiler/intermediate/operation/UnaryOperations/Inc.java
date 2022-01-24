package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Inc extends UnaryOperation {

    public Inc(Operand operand){
        super(OperationType.INC, operand);
    }

}