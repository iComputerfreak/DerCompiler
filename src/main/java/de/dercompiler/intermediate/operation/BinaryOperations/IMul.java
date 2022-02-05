package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.TernaryOperations.IMul3;

/**
 * In x86_64, this is a unary instruction /w target register EAX.
 * Register allocation has to deal with that later.
 */
public class IMul extends BinArithOperation {

    public IMul(Operand target, Operand source) {
        super(OperationType.IMUL, target, source, false);
    }

    @Override
    public IMul allocate(Operand target, Operand source){
        IMul mul = new IMul(target, source);
        mul.setMode(getMode());
        return mul;
    }

    public IMul3 allocateIMul3(Operand target, Operand source, ConstantValue constant){
        IMul3 mul = new IMul3(target, source, constant);
        mul.setMode(getMode());
        return mul;
    }
}
