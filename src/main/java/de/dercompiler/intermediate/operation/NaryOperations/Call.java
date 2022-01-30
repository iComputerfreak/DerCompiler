package de.dercompiler.intermediate.operation.NaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperations.Rol;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.selection.Datatype;

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

    public NaryOperation allocate(){
        return new Call((LabelOperand) getArgs()[0], true);
    }

    @Override
    public String getAtntSyntax(Datatype datatype) {
        return getIntelSyntax();
    }
}
