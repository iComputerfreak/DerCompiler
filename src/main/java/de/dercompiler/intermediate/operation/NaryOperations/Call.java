package de.dercompiler.intermediate.operation.NaryOperations;

import de.dercompiler.intermediate.operand.MethodReference;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.selection.IRMode;

import java.util.List;

public class Call extends NaryOperation {

    private List<IRMode> argsModes;

    public Call(MethodReference address, boolean isMemoryOperation, List<IRMode> argsModes) {
        super(OperationType.CALL, isMemoryOperation, address);
        setArgsModes(argsModes);
    }

    public Call(MethodReference method, boolean isMemoryOperation, List<IRMode> argsModes, Operand... args) {
        super(OperationType.CALL, isMemoryOperation, convertArgs(method, args));
        this.setArgsModes(argsModes);
    }

    private static Operand[] convertArgs(MethodReference method, Operand... args) {
        Operand[] allArgs = new Operand[args.length + 1];
        allArgs[0] = method;
        System.arraycopy(args, 0, allArgs, 1, args.length);
        return allArgs;
    }

    public Call allocate(){
        Call call = new Call((MethodReference) getArgs()[0], true, argsModes);
        call.setMode(getMode());
        call.setDefinition(getDefinition());
        call.setComment(getComment());
        return call;
    }

    public void setArgsModes(List<IRMode> argsModes) {
        this.argsModes = argsModes;
    }

    public IRMode getArgsMode(int i) {
        return this.argsModes.get(i);
    }

    private List<IRMode> getArgsModes() {
        return argsModes;
    }

}
