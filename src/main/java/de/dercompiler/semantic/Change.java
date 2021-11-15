package de.dercompiler.semantic;

/**
 * Represents a change of a definition for a given symbol in a given scope
 */
public class Change {
    private Symbol symbol;
    private Definition previousDefinition;
    private Scope previousScope;

    /**
     * Creates a new Change object
     * @param symbol The symbol that changed
     * @param previousDefinition The previous definition of that symbol that was overridden by the change
     * @param previousScope The previous scope, this definition was valid in
     */
    public Change(Symbol symbol, Definition previousDefinition, Scope previousScope) {
        this.symbol = symbol;
        this.previousDefinition = previousDefinition;
        this.previousScope = previousScope;
    }

    /**
     * Returns the symbol that changed
     */
    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Sets the changed symbol
     */
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the previous definition of this symbol that was overridden
     */
    public Definition getPreviousDefinition() {
        return previousDefinition;
    }

    /**
     * Sets the previous definition of this symbol that was overridden
     */
    public void setPreviousDefinition(Definition previousDefinition) {
        this.previousDefinition = previousDefinition;
    }

    /**
     * Returns the previous scope the definition was valid in
     */
    public Scope getPreviousScope() {
        return previousScope;
    }

    /**
     * Sets the previous scope of the symbol the definition was valid in
     */
    public void setPreviousScope(Scope previousScope) {
        this.previousScope = previousScope;
    }
}
