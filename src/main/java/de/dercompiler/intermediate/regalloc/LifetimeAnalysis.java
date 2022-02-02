package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;
import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.*;

import java.util.*;
import java.util.function.Consumer;

public class LifetimeAnalysis {

    public void updateOperand(Operand operand, Consumer<Integer> applyFunc) {
        if (operand instanceof VirtualRegister vr) {
            //we scip the first numbers because of parameters
            applyFunc.accept(CALL_ABI_NUM_ARGUMENTS + vr.getId());
        } else if (operand instanceof Address address) {
            //we call recursive
            if (Objects.nonNull(address.getBase())) {
                updateOperand(address.getBase(), applyFunc);
            }
            if (Objects.nonNull(address.getIndex())) {
                updateOperand(address.getIndex(), applyFunc);
            }
        } else if (operand instanceof ParameterRegister pr) {
            applyFunc.accept(pr.getId());
        } else {
            //ignore
        }
    }

    private final int CALL_ABI_NUM_ARGUMENTS;

    public LifetimeAnalysis(int CallAbiNumArguments) {
        CALL_ABI_NUM_ARGUMENTS = CallAbiNumArguments;
    }

    public VariableLifetimeTable analyze(Function function) {
        VariableLifetimeTable vlt = new VariableLifetimeTable(function, CALL_ABI_NUM_ARGUMENTS);
        int numberOfOperations = function.getOperations().size();
        if (numberOfOperations == 0) return vlt;

        Iterator<Operation> revIt = function.getOperations().descendingIterator();
        Operation op = revIt.next();
        for (var ref = new Object() {
            int i = 0;
        }; ref.i < numberOfOperations; ref.i++) {
            for (Operand operand : op.getArgs()) {
                updateOperand(operand, (id -> vlt.updateTarget(id, ref.i)));
            }
            updateOperand(op.getDefinition(), (id -> vlt.updateDefinition(id, ref.i)));
            if (revIt.hasNext()) {
                op = revIt.next();
            } else break;
        }

        return vlt.generate();
    }
}
