package de.dercompiler.intermediate.operation2.Operations;

import de.dercompiler.intermediate.operation2.BinaryOperation;
import de.dercompiler.intermediate.operation2.Operand.Adress;
import de.dercompiler.intermediate.operation2.Operand.ConstantValue;
import de.dercompiler.intermediate.operation2.Operand.Register;

public class Add extends BinaryOperation {

    public Add(Register target, Register source){
        super(target, source, "ADD");
    }

    public Add(Register target, Adress source){
        super(target, source, "ADD");
    }

    public Add(Adress target, Register source){
        super(target, source, "ADD");
    }

    public Add(Register target, ConstantValue source){
        super(target, source, "ADD");
    }

    public Add(Adress target, ConstantValue source){
        super(target, source, "ADD");
    }
}
