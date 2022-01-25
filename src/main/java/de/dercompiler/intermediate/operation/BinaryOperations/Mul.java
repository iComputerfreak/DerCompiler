package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;

/**
 * In x86_64, this is a unary instruction /w target register EAX.
 * Register allocation has to deal with that later.
 */
public class Mul extends BinArithOperation {

    public Mul(Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.MUL, target, source, isMemoryOperation);
    }

}
