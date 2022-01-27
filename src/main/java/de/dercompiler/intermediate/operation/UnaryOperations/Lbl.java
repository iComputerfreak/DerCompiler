package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Lbl extends UnaryOperation {
    private final LabelOperand label;

    public Lbl(LabelOperand operand) {
        super(OperationType.LBL, operand, true);
        this.label = operand;
    }

    @Override
    public String toString() {
        return this.label.toString() + ":";
    }
}
