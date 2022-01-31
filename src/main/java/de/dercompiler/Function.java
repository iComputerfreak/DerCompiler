package de.dercompiler;

import de.dercompiler.ast.MainMethod;
import de.dercompiler.ast.Method;
import de.dercompiler.intermediate.operation.Operation;
import firm.Graph;

import java.util.*;
import java.util.stream.IntStream;

public class Function {

    private final Method method;
    private List<Operation> operations;
    private int opCount;

    private final firm.Graph graph;

    public Function(Graph graph, Method method) {
        this.operations = null;
        this.graph = graph;
        this.opCount = 0;
        this.method = method;
    }

    public void setOperations(List<Operation> ops) {
        opCount = ops.size();
        IntStream.range(0, opCount).forEach(i -> ops.get(i).setIndex(i));
        operations = ops;
    }
    public List<Operation> getOperations() {
        return operations;
    }

    public String getName() { return graph.getEntity().getName(); }

    public int getNumVirtualRegisters() {
        return opCount;
    }

    public int getParamCount() {
        // one more for this, one less for String[] args
        return method.getParameters().size() + (method instanceof MainMethod ? -1 : 1);
    }

    public Graph getFirmGraph() {
        return graph;
    }
}
