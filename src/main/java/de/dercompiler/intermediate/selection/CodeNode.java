package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;

import java.util.List;

public class CodeNode {
    
    private final List<Operation> operations;

    public CodeNode(List<Operation> operations) {
        this.operations = operations;
    }

    public List<Operation> getOperations() {
        return operations;
    }
}
