package de.dercompiler.ast.statement;

import de.dercompiler.ast.expression.AbstractExpression;

public final class WhileStatement extends Statement {

    AbstractExpression condition;
    Statement statement;

    public WhileStatement(AbstractExpression condition, Statement statement) {
        this.condition = condition;
        this.statement = statement;
    }
}
