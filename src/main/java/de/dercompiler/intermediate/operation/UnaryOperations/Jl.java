package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jl extends JumpOperation {

    public Jl(LabelOperand address) {
        super(OperationType.JL, address);
    }

    @Override
    public Jge invert(LabelOperand otherTarget) {
        return new Jge(otherTarget);
    }

    @Override
    public Jl setTo(LabelOperand labelOperand) {
        return new Jl(labelOperand);
    }
}
