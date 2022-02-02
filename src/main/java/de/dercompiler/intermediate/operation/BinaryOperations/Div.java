package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public final class Div extends DivModOperation {

    public Div(Operand dividend, Operand divisor) {
        super(OperationType.DIV, dividend, divisor);
    }

    public Div allocate(Operand dividend, Operand divisor){
        throw new UnsupportedOperationException("This operation is for IR only!");
    }

}
