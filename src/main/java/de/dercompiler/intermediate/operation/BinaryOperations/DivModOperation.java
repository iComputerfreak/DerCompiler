package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

public abstract sealed class DivModOperation extends BinArithOperation permits Div, Mod {
    public DivModOperation(OperationType type, Operand target, Operand source) {
        super(type, target, source, true);
    }
}
