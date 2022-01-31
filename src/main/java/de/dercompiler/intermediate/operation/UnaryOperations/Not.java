package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Not extends UnaryArithmeticOperation {

    public Not(Operand operand, boolean isMemoryOperation) {
        super(OperationType.NOT, operand, isMemoryOperation);
    }

    @Override
    public UnaryArithmeticOperation allocate(Operand operand) {
        Not not = new Not(operand, true);
        not.setMode(getMode());
        return not;
    }
}
