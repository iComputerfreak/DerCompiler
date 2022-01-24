package de.dercompiler.intermediate.operation.NaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.ConstantOperation;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

/**
 * In x86_64, this is a constant operation.
 * RegisterAllocation has to deal with that later.
 */
public class Ret extends NaryOperation {

    public Ret(Operand result){
        super(OperationType.RET, result);
    }

    public Ret() {
        super(OperationType.RET);
    }
}
