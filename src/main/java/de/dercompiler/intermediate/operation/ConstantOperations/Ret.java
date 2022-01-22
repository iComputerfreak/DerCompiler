package de.dercompiler.intermediate.operation.ConstantOperations;

import de.dercompiler.intermediate.operation.ConstantOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Ret extends ConstantOperation {

    public Ret(){
        super(OperationType.RET);
    }
}
