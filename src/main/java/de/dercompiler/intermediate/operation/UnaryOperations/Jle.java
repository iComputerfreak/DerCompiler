package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Jle extends JumpOperation {

    public Jle(LabelOperand address) {
        super(OperationType.JLE, address);
    }

}
