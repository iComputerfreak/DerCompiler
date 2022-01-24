package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperations.BinArithOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

/**
 * In x86_64, this is a unary instruction /w target register EAX.
 * Register allocation has to deal with that later.
 */
public class Mul extends BinArithOperation {

    public Mul(Operand target, Operand source){
        super(OperationType.MUL, target, source);
    }

}