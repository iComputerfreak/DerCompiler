package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public abstract class JumpOperation extends UnaryOperation {
    public JumpOperation(OperationType operationType, Operand operand) {
        super(operationType, operand, true);
    }

    @Override
    public boolean needsDefinition() {
        return false;
    }
}
