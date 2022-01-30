package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.Signedness;

public class Jmp extends JumpOperation {

    public Jmp(LabelOperand address) {
        super(OperationType.JMP, address);
        this.setMode(Datatype.NODATA, Signedness.UNSIGNED);
    }

    public Jmp invert(LabelOperand otherTarget) {
        return new Jmp(otherTarget);
    }

    @Override
    public Jmp setTo(LabelOperand labelOperand) {
        return new Jmp(labelOperand);
    }
}
