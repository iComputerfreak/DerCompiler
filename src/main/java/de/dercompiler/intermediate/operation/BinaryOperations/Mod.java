package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public final class Mod extends DivModOperation {

    public Mod(Operand target, Operand source) {
        super(OperationType.MOD, target, source);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        throw new UnsupportedOperationException("This operation is for IR only!");
    }
}
