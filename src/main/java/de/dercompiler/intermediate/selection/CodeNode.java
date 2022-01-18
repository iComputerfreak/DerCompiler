package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;

import java.util.List;

public class CodeNode {
    
    private static int nextID = 0;
    
    public static int nextID() {
        return nextID++;
    }
    
    private final int id;
    private final FirmBlock firmBlock;
    private final List<Operation> operations;

    public CodeNode(List<Operation> operations, FirmBlock block) {
        this.id = CodeNode.nextID();
        this.operations = operations;
        this.firmBlock = block;
    }

    public int getId() {
        return id;
    }

    public FirmBlock getFirmBlock() {
        return firmBlock;
    }

    public List<Operation> getOperations() {
        return operations;
    }
}
