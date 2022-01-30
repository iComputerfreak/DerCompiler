package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;

public class Jne extends JumpOperation {

    public Jne(LabelOperand address) {
        super(OperationType.JNE, address);
    }

    @Override
    public Je invert(LabelOperand otherTarget) {
        return new Je(otherTarget);
    }

    @Override
    public Jne setTo(LabelOperand labelOperand) {
        return new Jne(labelOperand);
    }
}
