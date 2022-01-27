package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jb extends JumpOperation {

    public Jb(LabelOperand address) {
        super(OperationType.JB, address);
    }

}
