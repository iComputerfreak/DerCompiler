package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

public class Dec extends UnaryArithmeticOperation {

    public Dec(Operand operand, boolean isMemoryOperation) {
        super(OperationType.DEC, operand, isMemoryOperation);
    }


    @Override
    public UnaryArithmeticOperation allocate(Operand operand) {
        return new Dec(operand, true);
    }
}
