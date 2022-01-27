package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public abstract class BinArithOperation extends BinaryOperation {

    public BinArithOperation(OperationType type, Operand target, Operand source, boolean isMemoryOperation) {
        super(type, target, source, isMemoryOperation);
        if (!(target instanceof Register && source instanceof Register ||
                target instanceof Register && source instanceof Address ||
                target instanceof Address && source instanceof Register ||
                target instanceof Register && source instanceof ConstantValue ||
                target instanceof Address && source instanceof ConstantValue)) {
            //throw new RuntimeException("Incompatible operand types in %s node: %s %s".formatted(type.toString(), target.getIdentifier(), source.getIdentifier()));
        }
    }

}
