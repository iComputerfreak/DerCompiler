package de.dercompiler;

import de.dercompiler.intermediate.operation.Operation;
import firm.Graph;

import java.util.*;

public class Function {

    private List<Operation> operations;
    private int num;

    private final firm.Graph graph;

    public Function(firm.Graph graph) {
        operations = null;
        this.graph = graph;
        num = 0;
    }

    public void setOperations(List<Operation> ops) {
        int i = 0;
        Iterator<Operation> it = ops.iterator();
        while (it.hasNext()) {
            it.next().setIndex(i++);
        }
        operations = ops;
        num = i;
    }
    public List<Operation> getOperations() {
        return operations;
    }

    public String getName() { return graph.getEntity().getName(); }

    public int getNumVirtualRegisters() {
        return num;
    }

    public Graph getFirmGraph() {
        return graph;
    }
}
