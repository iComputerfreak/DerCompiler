package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

public class Cmp extends BinArithOperation {

    public Cmp(Operand target, Operand source) {
        super(OperationType.CMP, target, source, false);
    }

    @Override
    public boolean needsDefinition() {
        return false;
    }
}
