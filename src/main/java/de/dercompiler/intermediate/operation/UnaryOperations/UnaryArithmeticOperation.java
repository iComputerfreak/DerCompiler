package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public abstract class UnaryArithmeticOperation extends UnaryOperation {
    public UnaryArithmeticOperation(OperationType operationType, Operand operand, boolean isMemoryOperation) {
        super(operationType, operand, isMemoryOperation);
    }

    public abstract UnaryArithmeticOperation allocate(Operand operand);
}
