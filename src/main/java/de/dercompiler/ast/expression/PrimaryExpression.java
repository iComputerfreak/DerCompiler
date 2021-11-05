package de.dercompiler.ast.expression;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class PrimaryExpression extends AbstractExpression permits NullValue, ThisValue, BooleanValue, NewArrayExpression, NewObjectExpression, IntegerValue, Variable {
    protected PrimaryExpression(SourcePosition position) {
        super(position);
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitPrimaryExpression(this);
    }
}
