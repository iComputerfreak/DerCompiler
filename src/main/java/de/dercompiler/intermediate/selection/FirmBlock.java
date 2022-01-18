package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;
import firm.nodes.Block;

import java.util.LinkedList;
import java.util.List;

public class FirmBlock {
    
    private final int nr;
    private boolean visited;
    private final List<Operation> operations = new LinkedList<>();

    public FirmBlock(int nr) {
        this.nr = nr;
        this.visited = false;
    }
    
    public FirmBlock(Block block) {
        this(block.getNr());
    }

    public int getNr() {
        return nr;
    }

    public List<Operation> getOperations() {
        return operations;
    }
    
    public void addOperations(List<Operation> ops) {
        this.operations.addAll(ops);
    }

    public boolean getVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Override
    public String toString() {
        return "FirmBlock<" + nr + ">";
    }
}
