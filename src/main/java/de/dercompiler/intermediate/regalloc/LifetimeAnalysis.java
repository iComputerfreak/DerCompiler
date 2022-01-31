package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;
import de.dercompiler.intermediate.operation.Operation;

import java.util.*;

public class LifetimeAnalysis {

    public VariableLifetimeTable analyze(Function function) {
        VariableLifetimeTable vlt = new VariableLifetimeTable(function);
        int numberOfOperations = function.getOperations().size();
        if (numberOfOperations == 0) return vlt;

        Iterator<Operation> revIt = new LinkedList<>(function.getOperations()).descendingIterator();
        Operation op = revIt.next();
        for (int i = 0; i < numberOfOperations; i++) {

            //TODO: fix operands
            vlt.updateTarget(i, op.getIndex());

            if (revIt.hasNext()) {
                op = revIt.next();
            } else break;
        }

        return vlt.generate();
    }
}
