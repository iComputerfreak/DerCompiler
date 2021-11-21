package de.dercompiler.semantic;

/**
 * Represents a program scope, such as a class body or a function body
 */
public class Scope {
    private Scope parent;
    private int oldSize;
    private int level;

    public static final int PROGRAM_LEVEL = 0;
    public static final int CLASS_LEVEL = 1;
    public static final int METHOD_LEVEL = 2;

    /**
     * Creates a new Scope object
     * @param parent The parent scope which this scope is in
     * @param oldSize The number of changes that were present in the parent scope
     */
    public Scope(Scope parent, int oldSize) {
        this.parent = parent;
        this.oldSize = oldSize;
        this.level = parent == null? 0 : parent.level + 1;
    }

    /**
     * Returns the parent scope
     */
    public Scope getParent() {
        return parent;
    }

    /**
     * Sets the parent scope
     */
    public void setParent(Scope parent) {
        this.parent = parent;
    }

    /**
     * Returns the number of changes that were present in the parent scope
     */
    public int getOldSize() {
        return oldSize;
    }

    /**
     * Sets the number of changes that were present in the parent scope
     */
    public void setOldSize(int oldSize) {
        this.oldSize = oldSize;
    }

    public int getLevel() {
        return level;
    }
}
