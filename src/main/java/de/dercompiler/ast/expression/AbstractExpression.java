package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class AbstractExpression extends ASTNode permits BinaryExpression, ErrorExpression, PrimaryExpression, UnaryExpression, UninitializedValue, VoidExpression {

    protected AbstractExpression(SourcePosition position) { super(position); }
    
}
