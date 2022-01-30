package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jbe extends JumpOperation {

    public Jbe(LabelOperand address) {
        super(OperationType.JBE, address);
    }

    @Override
    public Ja invert(LabelOperand otherTarget) {
        return new Ja(otherTarget);
    }

    @Override
    public Jb setTo(LabelOperand labelOperand) {
        return new Jb(labelOperand);
    }
}
