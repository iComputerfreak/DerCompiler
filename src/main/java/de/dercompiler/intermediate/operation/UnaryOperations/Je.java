package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Je extends JumpOperation {

    public Je(LabelOperand address) {
        super(OperationType.JE, address);
    }

    public Jne invert(LabelOperand otherTarget) {
        return new Jne(otherTarget);
    }

    @Override
    public Je setTo(LabelOperand labelOperand) {
        return new Je(labelOperand);
    }
}
