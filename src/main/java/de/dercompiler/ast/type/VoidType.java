package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

public final class VoidType extends BasicType {
    
    public VoidType(SourcePosition position) { super(position);}

    @Override
    public boolean syntaxEquals(ASTNode other) {
        return other instanceof VoidType;
    }
}
