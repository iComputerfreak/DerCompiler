package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jg extends JumpOperation {

    public Jg(LabelOperand address) {
        super(OperationType.JG, address);
    }

    @Override
    public Jle invert(LabelOperand otherTarget) {
        return new Jle(otherTarget);
    }

    @Override
    public Jg setTo(LabelOperand labelOperand) {
        return new Jg(labelOperand);
    }
}
