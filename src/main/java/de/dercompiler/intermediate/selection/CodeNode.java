package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;

import java.util.List;

public class CodeNode {
    
    private static int nextID = 0;
    
    public static int nextID() {
        return nextID++;
    }
    
    private final int id;
    private final List<Operation> operations;

    public CodeNode(List<Operation> operations) {
        this.id = CodeNode.nextID();
        this.operations = operations;
    }

    public int getId() {
        return id;
    }

    public List<Operation> getOperations() {
        return operations;
    }
}
