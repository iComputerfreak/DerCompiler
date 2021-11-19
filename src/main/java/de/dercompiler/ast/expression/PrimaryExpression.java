package de.dercompiler.ast.expression;

import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class PrimaryExpression extends Expression permits NullValue, ThisValue, BooleanValue, NewArrayExpression, NewObjectExpression, IntegerValue, Variable {
    protected PrimaryExpression(SourcePosition position) {
        super(position);
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitPrimaryExpression(this);
    }
}
