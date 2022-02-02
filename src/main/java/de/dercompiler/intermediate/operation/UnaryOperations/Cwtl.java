package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public final class Cwtl extends UnaryArithmeticOperation {

    public Cwtl(Operand operand, boolean isMemoryOperation) {
        super(OperationType.CWTL, operand, isMemoryOperation);
    }

    @Override
    public Cwtl allocate(Operand operand) {
        Cwtl cwtl = new Cwtl(operand, false);
        cwtl.setDefinition(getDefinition());
        cwtl.setComment(getComment());
        cwtl.setMode(getMode());
        return cwtl;
    }
}
