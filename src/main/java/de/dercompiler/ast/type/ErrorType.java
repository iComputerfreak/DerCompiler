package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

/**
 * Represents a placeholder for a {@link Type} that could not be parsed
 */
public final class ErrorType extends BasicType {
    /**
     * Creates a new ErroType
     * @param position The source code position
     */
    public ErrorType(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        return false;
    }
}
