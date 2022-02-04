package de.dercompiler;

import de.dercompiler.ast.Method;
import de.dercompiler.intermediate.operation.Operation;
import firm.Graph;

import java.util.*;
import java.util.stream.IntStream;

public class Function {

    private final Method method;
    private LinkedList<Operation> operations;
    private int opCount;

    private final Graph graph;
    private int vrCount;

    public Function(Graph graph, Method method) {
        this.operations = null;
        this.graph = graph;
        this.opCount = 0;
        this.method = method;
    }

    public void setOperations(List<Operation> ops) {
        opCount = ops.size();
        IntStream.range(0, opCount).forEach(i -> ops.get(i).setIndex(i));
        operations = new LinkedList<>(ops);
    }

    public LinkedList<Operation> getOperations() {
        return operations;
    }

    public Operation getOperation(int index) {
        return operations.get(index);
    }

    public String getName() { return graph.getEntity().getName(); }

    public int getNumVirtualRegisters() {
        return vrCount;
    }

    public int getParamCount() {
        // one more for this, one less for String[] args
        return method.getParameters().size() + (method.isStatic() ? 0 : 1);
    }

    public Graph getFirmGraph() {
        return graph;
    }

    public boolean isStatic() {
        return method.isStatic();
    }

    public void setVrCount(int vrCount) {
        this.vrCount = vrCount;
    }

}
