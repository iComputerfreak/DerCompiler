package de.dercompiler.intermediate.selection;

import firm.nodes.Block;

public class FirmBlock {
    
    private final int nr;
    private boolean visited;

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
