package de.dercompiler.intermediate.operation.TernaryOperations;

import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.TenaryOperation;

public class IMul3 extends TenaryOperation {
    public IMul3(Operand destination, Operand factor, ConstantValue constant) {
        super(OperationType.IMUL3,  destination, factor, constant, false);
    }

    @Override
    public Operand getDefinition() {
        return getTarget();
    }
}
