package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Ja extends JumpOperation {

    public Ja(LabelOperand address) {
        super(OperationType.JA, address);
    }


    @Override
    public Jbe invert(LabelOperand otherTarget) {
        return new Jbe(otherTarget);
    }

    @Override
    public Ja setTo(LabelOperand labelOperand) {
        return new Ja(labelOperand);
    }
}
