package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.Signedness;

public class Push extends UnaryOperation {

    public Push(Operand operand) {
        super(OperationType.PUSH, operand, true);
        setMode(Datatype.QWORD, Signedness.UNSIGNED);
    }

}
