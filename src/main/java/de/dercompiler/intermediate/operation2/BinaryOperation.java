package de.dercompiler.intermediate.operation2;

import de.dercompiler.intermediate.operation2.Operand.Operand;

public abstract class BinaryOperation extends Operation{
    Operand target, source;
    String name;

    public BinaryOperation(Operand target, Operand source, String name){
        this.target = target;
        this.source = source;
        this.name = name;
    }

    public String getSyntax(){
        return name + " " + target.getIdentifier() + "," + source.getIdentifier();
    }
}
