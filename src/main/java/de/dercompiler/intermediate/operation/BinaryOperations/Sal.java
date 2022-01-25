package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
/*
Shift arithmetic left

Das rechte Argument ist entweder die Konstante 1 oder das CL Register
 */
public class Sal extends BinaryOperation {

    public Sal(Register target, Register source, boolean isMemoryOperation) {
        super(OperationType.SAL, target, source, isMemoryOperation);
    }

    public Sal(Register target, ConstantValue source, boolean isMemoryOperation) {
        super(OperationType.SAL, target, source, isMemoryOperation);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

    public Sal(Address target, Register source, boolean isMemoryOperation) {
        super(OperationType.SAL, target, source, isMemoryOperation);
    }

    public Sal(Address target, ConstantValue source, boolean isMemoryOperation) {
        super(OperationType.SAL, target, source, isMemoryOperation);
        if (source.getValue() != 1){
            throw new RuntimeException();
        }
    }

}
