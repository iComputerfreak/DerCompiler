package de.dercompiler.semantic;

/**
 * Represents a symbol in the program that has a definition and a scope in which the symbol is valid in
 */
public class Symbol {
    private String name;
    private Scope currentScope;
    private Definition currentDef;

    /**
     * Creates a new symbol with the given name, scope and definition
     * @param name The name of the symbol
     * @param currentScope The scope, the symbol was defined in
     * @param currentDef The definition of the symbol
     */
    public Symbol(String name, Scope currentScope, Definition currentDef) {
        this.name = name;
        this.currentScope = currentScope;
        this.currentDef = currentDef;
    }

    /**
     * Creates a new symbol when only the name is given
     * @param name The name of the symbol
     */
    public Symbol(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the symbol
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the symbol
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the current scope of the symbol
     */
    public Scope getCurrentScope() {
        return currentScope;
    }

    /**
     * Sets the current scope of the symbol
     */
    public void setCurrentScope(Scope currentScope) {
        this.currentScope = currentScope;
    }

    /**
     * Returns the current definition of the symbol
     */
    public Definition getCurrentDef() {
        return currentDef;
    }

    /**
     * Sets the current definition of the symbol
     */
    public void setCurrentDef(Definition currentDef) {
        this.currentDef = currentDef;
    }
}
