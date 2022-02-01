package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.selection.Datatype;

public class Movslq extends BinArithOperation{
    public Movslq(Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.MOVSLQ, target, source, isMemoryOperation);
    }

    public Movslq(Operand target, Operand source) {
        this(target, source, true);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source) {
        Movslq movslq = new Movslq(target, source, true);
        movslq.setMode(getMode());
        return movslq;
    }

    @Override
    public String getAtntSyntax(){
        return operationType.getAtntSyntax(Datatype.DWORD) +  " " + source.getIdentifier(Datatype.WORD) + "," + target.getIdentifier(Datatype.DWORD);
    }
}
