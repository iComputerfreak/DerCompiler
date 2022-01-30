package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Inc extends UnaryArithmeticOperation {

    public Inc(Operand operand, boolean isMemoryOperation) {
        super(OperationType.INC, operand, isMemoryOperation);
    }

    @Override
    public UnaryArithmeticOperation allocate(Operand operand) {
        return new Inc(operand, true);
    }
}
