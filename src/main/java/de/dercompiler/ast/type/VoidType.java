package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

import static de.dercompiler.lexer.token.TypeToken.VOID_TYPE;

/**
 * Represents a void type in MiniJava
 */
public final class VoidType extends BasicType {

    /**
     * Creates a new VoidType
     * @param position The source code position
     */
    public VoidType(SourcePosition position) { super(position);}

    @Override
    public boolean syntaxEquals(ASTNode other) {
        return other instanceof VoidType;
    }

    @Override
    public String toString() {
        return VOID_TYPE.getId();
    }
}
