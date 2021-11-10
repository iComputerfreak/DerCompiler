package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

import static de.dercompiler.lexer.token.TypeToken.INT_TYPE;

/**
 * Represents an integer type in MiniJava
 */
public final class IntType extends BasicType {

    /**
     * Creates a new IntType
     * @param position The source code position
     */
    public IntType(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        return other instanceof IntType;
    }

    @Override
    public String toString() {
        return INT_TYPE.getId();
    }


}
