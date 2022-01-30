package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jge extends JumpOperation {

    public Jge(LabelOperand address) {
        super(OperationType.JGE, address);
    }

    @Override
    public Jl invert(LabelOperand otherTarget) {
        return new Jl(otherTarget);
    }

    @Override
    public Jge setTo(LabelOperand labelOperand) {
        return new Jge(labelOperand);
    }
}
