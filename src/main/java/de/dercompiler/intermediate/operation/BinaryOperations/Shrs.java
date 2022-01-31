package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

/*
Shift right

Das rechte Argument ist entweder die Konstante 1, ein 1-byte immediate value oder das CL Register
 */
public class Shrs extends BinaryOperation {

    public Shrs(Operand target, Operand source) {
        super(OperationType.SHRS, target, source, false);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        Shrs shrs = new Shrs(target, source);
        shrs.setMode(getMode());
        return shrs;
    }
}
