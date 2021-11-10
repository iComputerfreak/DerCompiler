package de.dercompiler.semantic;

import java.util.Stack;

/**
 * Represents a table to store symbols and their definitions in various scopes of the program
 */
public class SymbolTable {
    
    private final Stack<Change> changes;
    private Scope currentScope = null;

    /**
     * Creates a new SymbolTable in the given scope
     * @param currentScope The scope the symbol table is initialized with
     */
    public SymbolTable(Scope currentScope) {
        this.changes = new Stack<>();
        this.currentScope = currentScope;
    }

    /**
     * Returns the current stack of changes stored in this symbol table
     */
    public Stack<Change> getChanges() {
        return changes;
    }

    /**
     * Returns the current scope of the symbol table
     */
    public Scope getCurrentScope() {
        return currentScope;
    }

    /**
     * Sets the current scope of the symbol table
     */
    public void setCurrentScope(Scope currentScope) {
        this.currentScope = currentScope;
    }

    /**
     * Enters a new scope and sets the current scope as parent
     */
    public void enterScope() {
        currentScope = new Scope(currentScope, changes.size());
    }

    /**
     * Leaves the current scope, restoring all changes and returning to the parent scope
     */
    public void leaveScope() {
        while (changes.size() > currentScope.getOldSize()) {
            Change c = changes.pop();
            c.getSymbol().setCurrentDef(c.getPreviousDefinition());
            c.getSymbol().setCurrentScope(c.getPreviousScope());
        }
        currentScope = currentScope.getParent();
    }

    /**
     * Inserts a new definition in the symbol table
     * @param symbol The symbol for which the definition has changed
     * @param definition The new definition
     */
    public void insert(Symbol symbol, Definition definition) {
        changes.push(new Change(symbol, symbol.getCurrentDef(), symbol.getCurrentScope()));
        symbol.setCurrentDef(definition);
        symbol.setCurrentScope(currentScope);
    }

    /**
     * Looks up a given symbol in the symbol table
     * @param symbol The symbol to look up
     * @return The current definition of this symbol
     */
    public Definition lookup(Symbol symbol) {
        return symbol.getCurrentDef();
    }

    /**
     * Checks, whether the given symbol is currently stored in the symbol table
     * @param symbol The symbol to check for
     * @return Whether the symbol is stored in the currenty symbol table (taking into account the current scope)
     */
    public boolean isDefinedInCurrentScope(Symbol symbol) {
        return symbol.getCurrentScope() == currentScope;
    }
    
}
