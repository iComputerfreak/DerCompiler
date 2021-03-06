package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

import static de.dercompiler.lexer.token.TypeToken.BOOLEAN_TYPE;

/**
 * Represents a boolean type in MiniJava
 */
public final class BooleanType extends BasicType {

    /**
     * Creates a new BooleanType
     * @param position The source code position
     */
    public BooleanType(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        return other instanceof BooleanType;
    }

    @Override
    public String toString() {
        return BOOLEAN_TYPE.getId();
    }
}
