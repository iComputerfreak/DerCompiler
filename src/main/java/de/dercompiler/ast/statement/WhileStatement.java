package de.dercompiler.ast.statement;

import de.dercompiler.ast.SyntaxEquatable;
import de.dercompiler.ast.expression.AbstractExpression;

import java.util.Objects;

public final class WhileStatement extends Statement {

    AbstractExpression condition;
    Statement statement;

    public WhileStatement(AbstractExpression condition, Statement statement) {
        this.condition = condition;
        this.statement = statement;
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof WhileStatement ws) {
            return condition.syntaxEquals(ws.condition)
                    && statement.syntaxEquals(ws.statement);
        }
        return false;
    }
}
