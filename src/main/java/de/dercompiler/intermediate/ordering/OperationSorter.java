package de.dercompiler.intermediate.ordering;

import de.dercompiler.intermediate.Operand;
import de.dercompiler.intermediate.operation.BinaryOperationType;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.OperationType;

import java.util.*;

/**
 * Represents a strategy for ordering IR operations of a basic block.
 */
public abstract class OperationSorter {

    private List<OperationData> operationData;
    private List<OperationData> readyOperations;

    public OperationSorter(List<Operation> ops) {
        // determine dependencies between operations and collect data
        this.analyse(ops);

        // determine "roots" of operation tree, i.e. ready operations
        this.initializeReadyOps();
    }

    /**
     * Determines any {@link Dependency} between {@link Operation}s of various {@link DependencyType}s that might occur in the given list of Operations.
     * @param ops list of {@link Operation}s in a basic block structure, i.e. this list shall be executed completely or not at all
     */
    public void analyse(List<Operation> ops) {
        RegisterMgmt mgmt = new RegisterMgmt();
        operationData = new ArrayList<>(ops.size());

        for (int i = ops.size() - 1; i >= 0; i--) {
            Operation op = ops.get(i);
            OperationData opData = new OperationData(op, i, new LinkedList<>());
            if (i == 0) opData.setDepth(0);
            operationData.set(i, opData);

            if (op.getOperationType() == BinaryOperationType.LOAD) {
                Operand loaded = op.getArgs()[1];

                int storeOpNr = mgmt.getCurrStore(loaded);
                addDependency(opData, storeOpNr, DependencyType.LOAD_STORE);

                mgmt.setCurrLoad(loaded, i);
            } else if (op.getOperationType() == BinaryOperationType.STORE) {
                Operand stored = op.getArgs()[0];

                int storeOpNr = mgmt.getCurrStore(stored);
                addDependency(opData, storeOpNr, DependencyType.STORE_STORE);

                int loadOpNr = mgmt.getCurrLoad(stored);
                addDependency(opData, loadOpNr, DependencyType.STORE_LOAD);

                mgmt.setCurrStore(stored, i);
            } else {
                for (Operand operand : op.getArgs()) {
                    addDependency(opData, mgmt.getCurrLoad(operand), DependencyType.DEF_USE);
                }
            }

        }
    }

    /**
     * Registers a forward dependency from the given operation to another operation indicated by its index.
     * @param opData "source" operation of the dependency relation
     * @param toOperationNumber index of the "target" operation of the dependency relation
     * @param type type of the dependency
     */
    private void addDependency(OperationData opData, int toOperationNumber, DependencyType type) {
        if (toOperationNumber < 0) return;
        OperationData toOperationData = operationData.get(toOperationNumber);
        opData.addDependency(toOperationData, type);
        opData.addAll(toOperationData.getDependencies(), DependencyType.TRANSITIVE);
    }

    /**
     *  Determines the initial set of ready operations in the list of operations.
     */
    private void initializeReadyOps() {
        List<OperationData> candidates = new ArrayList<>(operationData);
        // Sieve of Erastothenes-type algorithm
        for (int idx = 0; idx < candidates.size(); idx++) {
            // if op has survived until here, it is a root
            // remove all its successors
            candidates.removeAll(candidates.get(idx).getSuccessors());
        }

        this.readyOperations = candidates;
    }

    /**
     * Removes the given {@link Operation} from the internal list of ready operations and replaces it with all of its ready successors.
     * @param nextOperation {@link Operation} chosen to be executed next
     */
    public void choose(OperationData nextOperation) {
        if (!isReady(nextOperation)) {
            // Error!
        }

        readyOperations.remove(nextOperation);

        // Now, which of the successors of nextOperation are ready?
        List<OperationData> candidates = nextOperation.getDirectSuccessors();

        int readyIdx = 0, readyNr = readyOperations.get(0).number;
        int candidateIdx = 0, candidateNr = candidates.get(0).number;
        Set<OperationData> dependentSuccessors = new HashSet<>();
        while (true) {
            // Use linear order: if op1.nr < op2.nr, then there cannot be a dependency from op2 to op1
            // Use completeness of the ready set: any operation that is not enqueued yet must be the successor of one ready operation
            if (readyNr < candidateNr) {
                OperationData readyOp = readyOperations.get(readyIdx);
                dependentSuccessors.addAll(readyOp.getDirectSuccessors());
                readyIdx++;
                readyNr = readyIdx < readyOperations.size() ? readyOperations.get(readyIdx).number : -1;
            } else {
                OperationData candidateOp = candidates.get(candidateIdx);
                if (!dependentSuccessors.contains(candidateOp)) {
                    readyOperations.add(readyIdx, candidateOp);
                }
                candidateIdx++;
                if (candidateIdx >= candidates.size()) break;
                candidateNr = candidates.get(candidateIdx).number;
            }
        }
    }

    /**
     * Returns true iff all the predecessors in the dependency graph have been "chosen" before.
     * @param operation The operation for which to determine if it is ready or not
     * @return true if the operation is contained in the list of ready operations
     */
    public boolean isReady(OperationData operation) {
        return this.readyOperations.contains(operation);
    }

    protected Collection<OperationData> getReadyOps() {
        return List.copyOf(this.readyOperations);
    }

    record RegisterDemand(int min, int max) {
    }

    /**
     *  For any {@link Operand}, stores the operations that have loaded or stored it
     */
    static class RegisterMgmt {

        HashMap<Operand, Integer> currLoad;
        HashMap<Operand, Integer> currStore;

        RegisterMgmt() {
            currLoad = new HashMap<>();
            currStore = new HashMap<>();
        }

        public int getCurrLoad(Operand register) {
            return this.currLoad.getOrDefault(register, -1);
        }

        public void setCurrLoad(Operand register, int loadOp) {
            this.currLoad.put(register, loadOp);
        }

        public int getCurrStore(Operand register) {
            return this.currStore.getOrDefault(register, -1);
        }

        public void setCurrStore(Operand register, int loadOp) {
            this.currStore.put(register, loadOp);
        }
    }

    static final class OperationData {
        private final Operation operation;
        private final int number;
        private final List<Dependency> dependencies;
        private int depth;

        OperationData(Operation operation, int number, List<Dependency> dependencies) {
            this.operation = operation;
            this.number = number;
            this.dependencies = dependencies;
            this.depth = Integer.MAX_VALUE;
        }

        void addDependency(OperationData toOperation, DependencyType type) {
            dependencies.add(new Dependency(toOperation, type));
            setDepth(Math.min(depth, toOperation.getDepth() + 1));
        }

        public void addAll(List<Dependency> dependencies, DependencyType type) {
            dependencies.forEach(d -> addDependency(d.opTo, type));
        }

        public List<OperationData> getSuccessors() {
            return dependencies.stream().map(Dependency::opTo).toList();
        }

        public List<OperationData> getDirectSuccessors() {
            return dependencies.stream().filter(d -> d.type != DependencyType.TRANSITIVE).map(Dependency::opTo).toList();
        }

        public OperationType getOperationType() {
            return operation.getOperationType();
        }

        public Operation getOperation() {
            return operation;
        }

        public int getNumber() {
            return number;
        }

        public List<Dependency> getDependencies() {
            return dependencies;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public int getDepth() {
            return depth;
        }
    }

    /**
     * Represents a dependency forward in the list of operations
     */
    record Dependency(OperationData opTo, DependencyType type) {}

    enum DependencyType { LOAD_STORE, STORE_STORE, STORE_LOAD, TRANSITIVE, DEF_USE }
}
