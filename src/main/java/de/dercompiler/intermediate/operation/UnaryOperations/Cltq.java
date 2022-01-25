package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public final class Cltq extends UnaryOperation {

    public Cltq(Operand operand) {
        super(OperationType.CLTQ, operand);
    }

}
