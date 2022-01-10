package de.dercompiler.intermediate.operation2.Operations;

import de.dercompiler.intermediate.operation2.Operand.Label;
import de.dercompiler.intermediate.operation2.UnaryOperation;

public class Jump extends UnaryOperation {
    public Jump(Label label){
        super(label, "JMP");
    }
}
