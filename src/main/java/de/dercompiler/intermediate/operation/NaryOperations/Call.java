package de.dercompiler.intermediate.operation.NaryOperations;

import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;
import firm.nodes.Node;

import java.util.Arrays;

public class Call extends NaryOperation {

    public Call(LabelOperand address){
        super(OperationType.CALL, address);
    }

    public Call(LabelOperand method, Operand... args) {
        super(OperationType.CALL, convertArgs(method, args));
    }

    private static Operand[] convertArgs(LabelOperand method, Operand... args) {
        Operand[] allArgs = new Operand[args.length + 1];
        allArgs[0] = method;
        System.arraycopy(args, 0, allArgs, 1, args.length);
        return allArgs;
    }
}