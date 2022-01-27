package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Ja extends JumpOperation {

    public Ja(LabelOperand address) {
        super(OperationType.JA, address);
    }

}
