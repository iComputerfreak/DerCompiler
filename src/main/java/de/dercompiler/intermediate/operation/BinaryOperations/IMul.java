package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

/**
 * In x86_64, this is a unary instruction /w target register EAX.
 * Register allocation has to deal with that later.
 */
public class IMul extends BinArithOperation {

    public IMul(Operand target, Operand source) {
        super(OperationType.IMUL, target, source, false);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        IMul mul = new IMul(target, source);
        mul.setMode(getMode());
        return mul;
    }
}
