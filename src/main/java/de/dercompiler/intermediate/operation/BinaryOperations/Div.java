package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperations.BinArithOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.selection.Datatype;

public class Div extends BinArithOperation {

    public Div(Operand target, Operand source) {
        super(OperationType.DIV, target, source, true);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        return new Div(target, source);
    }

    @Override
    public String getIntelSyntax() {
        return operationType.getSyntax() + " " + target.getIdentifier();
    }

    public String getAtntSyntax(Datatype datatype){
        return getIntelSyntax();
    }
}
