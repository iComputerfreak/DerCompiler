package de.dercompiler.intermediate.operation.NaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Call extends NaryOperation {

    public Call(LabelOperand address, boolean isMemoryOperation) {
        super(OperationType.CALL, isMemoryOperation, address);
    }

    public Call(LabelOperand method, boolean isMemoryOperation, Operand... args) {
        super(OperationType.CALL, isMemoryOperation, convertArgs(method, args));
    }

    private static Operand[] convertArgs(LabelOperand method, Operand... args) {
        Operand[] allArgs = new Operand[args.length + 1];
        allArgs[0] = method;
        System.arraycopy(args, 0, allArgs, 1, args.length);
        return allArgs;
    }
}
