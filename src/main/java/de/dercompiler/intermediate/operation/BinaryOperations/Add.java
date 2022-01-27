package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

public class Add extends BinArithOperation {

    public Add(Operand target, Operand source) {
        super(OperationType.ADD, target, source, false);
    }

}
