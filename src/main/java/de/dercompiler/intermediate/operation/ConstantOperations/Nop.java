package de.dercompiler.intermediate.operation.ConstantOperations;

import de.dercompiler.intermediate.operation.ConstantOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Nop extends ConstantOperation {

    public Nop(){
        super(OperationType.NOP, false);
    }
}
