package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import firm.Mode;

/*
Rotate right

Das rechte Argument ist entweder die Konstante 1 oder das CL Register
 */
public class Ror extends ShiftOperation {

    public Ror(Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.ROR, target, source, isMemoryOperation);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        Ror ror = new Ror(target, source, true);
        ror.setMode(getMode());
        return ror;
    }
}