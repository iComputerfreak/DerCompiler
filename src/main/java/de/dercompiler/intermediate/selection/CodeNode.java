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
    private final boolean isPhi;

    public CodeNode(List<Operation> operations, FirmBlock block, boolean isPhi) {
        this.id = CodeNode.nextID();
        this.operations = operations;
        this.firmBlock = block;
        this.isPhi = isPhi;
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
    
    public boolean isPhi() {
        return isPhi;
    }
}
