package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Xchg extends BinaryOperation {

    public Xchg(Register target, Register source, boolean isMemoryOperation) {
        super(OperationType.XCHG, target, target, source, isMemoryOperation);
    }

    public Xchg(Register target, Address source, boolean isMemoryOperation) {
        super(OperationType.XCHG, target, target, source, isMemoryOperation);
    }

    public Xchg(Address target, Register source, boolean isMemoryOperation) {
        super(OperationType.XCHG, target, target, source, isMemoryOperation);
    }
}
