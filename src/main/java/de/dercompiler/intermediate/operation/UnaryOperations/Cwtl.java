package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.selection.Datatype;

public final class Cwtl extends UnaryOperation {

    public Cwtl(Operand operand) {
        super(OperationType.CWTL, operand);
    }
}
