package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.Signedness;

public class Lea extends BinaryOperation {
    public Lea(Operand target, Operand addr) {
        super(OperationType.LEA, target, addr, true);
        setMode(Datatype.QWORD, Signedness.UNSIGNED);
        setDefinition(target);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source) {
        return new Lea(target, source);
    }
}
