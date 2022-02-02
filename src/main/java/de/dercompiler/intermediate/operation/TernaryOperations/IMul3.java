package de.dercompiler.intermediate.operation.TernaryOperations;

import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class IMul3 extends NaryOperation {
    public IMul3(ConstantValue constant, Operand factor, Operand destination) {
        super(OperationType.IMUL3, false, constant, factor, destination);
    }

}
