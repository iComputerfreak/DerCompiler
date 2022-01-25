package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

public class Xor extends BinArithOperation {

    public Xor(Operand definition, Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.XOR, definition, target, source, isMemoryOperation);
    }

}
