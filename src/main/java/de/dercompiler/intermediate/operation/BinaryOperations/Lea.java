package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Lea extends BinaryOperation {
    public Lea(Operand target, Operand addr) {
        super(OperationType.LEA, target, addr, true);
        setDefinition(target);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source) {
        return null;
    }
}
