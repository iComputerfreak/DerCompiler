package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.Signedness;

public class Jmp extends UnaryOperation {

    public Jmp(LabelOperand address){
        super(OperationType.JMP, address);
        this.setMode(Datatype.OTHER, Signedness.UNSIGNED);
    }

}