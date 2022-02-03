package de.dercompiler.intermediate.regalloc.analysis;

import de.dercompiler.Function;
import de.dercompiler.intermediate.operation.NaryOperations.Call;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;

public class FunctionDensityOptimizer {

    public FunctionSplitView analyse(Function func) {
        FunctionSplitView fsv = new FunctionSplitView(func);
        for (Operation op : func.getOperations()) {
            if (op instanceof Call call) {
                fsv.split(call.getIndex());
            } else if (op instanceof Ret ret && ret != func.getOperations().getLast()) {
                fsv.split(ret.getIndex());
            }
        }
        fsv.calculateInformation();
        return fsv;
    }
}
