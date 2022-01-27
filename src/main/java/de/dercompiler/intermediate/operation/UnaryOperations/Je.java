package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Je extends JumpOperation {

    public Je(LabelOperand address) {
        super(OperationType.JE, address);
    }

}
