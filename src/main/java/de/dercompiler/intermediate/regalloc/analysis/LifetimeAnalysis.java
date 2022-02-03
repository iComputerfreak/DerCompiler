package de.dercompiler.intermediate.regalloc.analysis;

import de.dercompiler.Function;
import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.*;

import java.util.*;
import java.util.function.Consumer;

public class LifetimeAnalysis {

    public void updateOperand(Operand operand, Consumer<IRRegister> applyFunc) {
        if (operand instanceof VirtualRegister vr) {
            //we scip the first numbers because of parameters
            applyFunc.accept(vr);
        } else if (operand instanceof Address address) {
            //we call recursive
            if (Objects.nonNull(address.getBase())) {
                updateOperand(address.getBase(), applyFunc);
            }
            if (Objects.nonNull(address.getIndex())) {
                updateOperand(address.getIndex(), applyFunc);
            }
        } else if (operand instanceof ParameterRegister pr) {
            applyFunc.accept(pr);
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
                updateOperand(operand, (irr -> vlt.updateTarget(irr, ref.i)));
            }
            if (op.hasDefinition()) {
                updateOperand(op.getDefinition(), (irr -> vlt.updateDefinition(irr, ref.i)));
            }
            if (revIt.hasNext()) {
                op = revIt.next();
            } else break;
        }

        return vlt.generate();
    }
}
