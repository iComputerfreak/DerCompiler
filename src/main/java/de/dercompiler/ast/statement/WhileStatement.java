package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
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
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof WhileStatement ws) {
            return condition.syntaxEqual(ws.condition)
                    && statement.syntaxEqual(ws.statement);
        }
        return false;
    }
}