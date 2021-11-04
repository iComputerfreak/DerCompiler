package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class AbstractExpression implements ASTNode permits BinaryExpression, ErrorExpression, PrimaryExpression, UnaryExpression, UninitializedValue, VoidExpression {

    private SourcePosition position;

    protected AbstractExpression(SourcePosition position) { this.position = position; }

    @Override
    public SourcePosition getSourcePosition() {
        return position;
    }

}
