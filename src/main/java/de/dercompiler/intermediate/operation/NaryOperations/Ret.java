package de.dercompiler.intermediate.operation.NaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

/**
 * In x86_64, this is a constant operation.
 * RegisterAllocation has to deal with that later.
 */
public class Ret extends NaryOperation {

    public Ret(Operand result, boolean isMemoryOperation){
        super(OperationType.RET, isMemoryOperation, result);
    }

    public Ret(boolean isMemoryOperation) {
        super(OperationType.RET, isMemoryOperation);
    }
}
