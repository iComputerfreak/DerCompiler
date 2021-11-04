package de.dercompiler.ast.expression;

import de.dercompiler.lexer.SourcePosition;

public abstract sealed class PrimaryExpression extends AbstractExpression permits NullValue, ThisValue, BooleanValue, NewArrayExpression, NewObjectExpression, IntegerValue, Variable {
    protected PrimaryExpression(SourcePosition position) {
        super(position);
    }

}
