package de.dercompiler.ast.statement;

import de.dercompiler.ast.expression.AbstractExpression;

public final class ReturnStatement extends Statement {

    AbstractExpression returnExpression;

    public ReturnStatement(AbstractExpression returnExpression) {
        this.returnExpression = returnExpression;
    }
}
