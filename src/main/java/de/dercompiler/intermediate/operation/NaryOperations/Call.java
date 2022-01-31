package de.dercompiler.intermediate.operation.NaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.MethodReference;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperations.Rol;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.selection.Datatype;

public class Call extends NaryOperation {

    public Call(MethodReference address, boolean isMemoryOperation) {
        super(OperationType.CALL, isMemoryOperation, address);
    }

    public Call(MethodReference method, boolean isMemoryOperation, Operand... args) {
        super(OperationType.CALL, isMemoryOperation, convertArgs(method, args));
    }

    private static Operand[] convertArgs(MethodReference method, Operand... args) {
        Operand[] allArgs = new Operand[args.length + 1];
        allArgs[0] = method;
        System.arraycopy(args, 0, allArgs, 1, args.length);
        return allArgs;
    }

    public Call allocate(){
        return new Call((MethodReference) getArgs()[0], true);
    }

    @Override
    public String getAtntSyntax() {
        return getIntelSyntax();
    }
}
