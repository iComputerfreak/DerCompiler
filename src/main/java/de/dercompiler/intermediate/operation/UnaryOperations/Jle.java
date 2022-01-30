package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jle extends JumpOperation {

    public Jle(LabelOperand address) {
        super(OperationType.JLE, address);
    }

    @Override
    public Jg invert(LabelOperand otherTarget) {
        return new Jg(otherTarget);
    }

    @Override
    public Jle setTo(LabelOperand labelOperand) {
        return new Jle(labelOperand);
    }
}
