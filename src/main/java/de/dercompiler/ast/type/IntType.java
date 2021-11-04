package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

public final class IntType extends BasicType {
    
    public IntType(SourcePosition position) {super(position);}

    @Override
    public boolean syntaxEquals(ASTNode other) {
        return other instanceof IntType;
    }
}
