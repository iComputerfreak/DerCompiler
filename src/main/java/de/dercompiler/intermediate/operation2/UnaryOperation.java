package de.dercompiler.intermediate.operation2;

import de.dercompiler.intermediate.operation2.Operand.Operand;

public abstract class UnaryOperation {
    Operand operand;
    String name;

    public UnaryOperation(Operand oerand, String name){
        this.operand = operand;
        this.name = name;
    }

    public String getSyntax(){
        return name + " " + operand.getIdentifier();
    }
}
