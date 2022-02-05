package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Cmp extends BinArithOperation {

    public Cmp(Operand left, Operand right) {
        super(OperationType.CMP, left, right, false);
    }

    @Override
    public boolean needsDefinition() {
        return false;
    }

    @Override
    public Cmp allocate(Operand left, Operand right){
        Cmp cmp = new Cmp(left, right);
        cmp.setMode(getMode());
        return cmp;
    }

}
