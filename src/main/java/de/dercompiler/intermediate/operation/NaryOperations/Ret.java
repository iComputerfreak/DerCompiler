package de.dercompiler.intermediate.operation.NaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.selection.Datatype;

/**
 * In x86_64, this is a constant operation.
 * RegisterAllocation has to deal with that later.
 */
public class Ret extends NaryOperation {

    public Ret(Operand result){
        super(OperationType.RET, true, result);
    }

    public Ret() {
        super(OperationType.RET, true);
    }

    @Override
    public boolean needsDefinition() {
        return getArgsCount() > 0;
    }


}
