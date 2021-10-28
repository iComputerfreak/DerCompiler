package de.dercompiler.ast.statement;

import de.dercompiler.ast.expression.AbstractExpression;

public final class ExpressionStatement extends Statement {

    AbstractExpression expressionRoot;

    public ExpressionStatement(AbstractExpression expression) {
        expressionRoot = expression;
    }
}
