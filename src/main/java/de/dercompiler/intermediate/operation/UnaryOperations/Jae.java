package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jae extends JumpOperation {

    public Jae(LabelOperand address) {
        super(OperationType.JAE, address);
    }

    @Override
    public Jb invert(LabelOperand otherTarget) {
        return new Jb(otherTarget);
    }

    @Override
    public Jae setTo(LabelOperand labelOperand) {
        return new Jae(labelOperand);
    }
}
