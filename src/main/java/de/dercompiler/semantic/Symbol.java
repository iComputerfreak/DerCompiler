package de.dercompiler.semantic;

import de.dercompiler.ast.expression.ASTDefinition;

/**
 * Represents a symbol in the program that has a definition and a scope in which the symbol is valid in
 */
public class Symbol {
    private String name;
    private Scope currentScope;
    private ASTDefinition currentDef;



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
    public ASTDefinition getCurrentDef() {
        return currentDef;
    }

    /**
     * Sets the current definition of the symbol
     */
    public void setCurrentDef(ASTDefinition currentDef) {
        this.currentDef = currentDef;
    }
}
