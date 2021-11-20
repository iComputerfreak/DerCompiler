package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

/**
 * Represents a node in the abstract syntax tree
 */
public abstract class ASTNode {
    
    private final SourcePosition sourcePosition;

    /**
     * Creates a new ASTNode with the given source position
     * @param sourcePosition The position in the source code
     */
    public ASTNode(SourcePosition sourcePosition) {
        this.sourcePosition = sourcePosition;
    }

    /**
     * Checks, whether the given node and this node are syntactically equal
     * @param other The other ASTNode
     * @return true if the nodes are syntactically equal
     */
    public boolean syntaxEquals(ASTNode other) {
        return other != null;
    }

    /**
     * Returns the position of this node in the source code
     */
    public SourcePosition getSourcePosition() {
        return this.sourcePosition;
    }

    /**
     * Accept a given visitor for this node
     * @param astNodeVisitor The visitor to accept
     */
    public abstract void accept(ASTNodeVisitor astNodeVisitor);
}
