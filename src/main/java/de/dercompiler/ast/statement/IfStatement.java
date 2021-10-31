package de.dercompiler.ast.statement;

import de.dercompiler.ast.expression.AbstractExpression;

import java.util.Objects;

public final class IfStatement extends Statement {

    AbstractExpression condition;
    Statement thenStatement;
    Statement elseStatement;

    public IfStatement(AbstractExpression condition, Statement thenStatement, Statement elseStatement) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    public boolean hasElse() {
        return Objects.nonNull(elseStatement);
    }
}
