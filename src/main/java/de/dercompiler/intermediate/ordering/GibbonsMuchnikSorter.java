package de.dercompiler.intermediate.ordering;

import de.dercompiler.intermediate.operation.Operation;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class GibbonsMuchnikSorter extends OperationSorter {

    public GibbonsMuchnikSorter(List<Operation> ops) {
        super(ops);
    }

    public List<Operation> get() {
        List<Operation> basicBlockOps = new ArrayList<>();

        Collection<OperationData> readyOps;
        while (!(readyOps = this.getReadyOps()).isEmpty()) {
            // (1) Choose load operations
            OperationData loadOperation = readyOps.stream()
                    .filter(op -> op.getOperationType() == BinaryOperationType.LOAD)
                    .findFirst().orElse(null);
            if (!Objects.isNull(loadOperation)) {
                basicBlockOps.add(loadOperation.getOperation());
                choose(loadOperation);
                continue;
            }

            // (2) Sort by numbers of direct successors
            AtomicReference<Integer> maxNumberOfSuccs = new AtomicReference<>();
            OperationData next = readyOps.stream()
                    // sort by out-degree, descending
                    .sorted(Comparator.comparingInt(op -> -op.getDirectSuccessors().size()))
                    // now peek gives operation with most successors
                    .peek(op -> maxNumberOfSuccs.set(op.getDirectSuccessors().size()))
                    // get all operations with maximal successor count
                    .takeWhile(op -> op.getDirectSuccessors().size() == maxNumberOfSuccs.get())

            // (3) Sort by distance to last operation
                    .min(Comparator.comparingInt(op -> -op.getDepth())).orElse(null);

            // cannot be true since readyOps is not empty
            assert next != null;
            basicBlockOps.add(next.getOperation());
            choose(next);

        }

        return basicBlockOps;
    }
}
