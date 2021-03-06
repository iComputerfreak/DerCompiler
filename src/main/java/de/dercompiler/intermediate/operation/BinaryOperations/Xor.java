package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Xor extends BinArithOperation {

    public Xor(Operand target, Operand source) {
        super(OperationType.XOR, target, source, false);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        Xor xor = new Xor(target, source);
        xor.setMode(getMode());
        return xor;
    }
}
