package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Div extends BinArithOperation {

    public Div(Operand target, Operand source) {
        super(OperationType.DIV, target, source, true);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        Div div = new Div(target, source);
        div.setMode(getMode());
        return div;
    }

    @Override
    public String getIntelSyntax() {
        return operationType.getSyntax() + " " + target.getIdentifier();
    }

    @Override
    public String getAtntSyntax(){
        return getIntelSyntax();
    }
}
