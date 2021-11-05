package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class WhileStatement extends Statement {

    AbstractExpression condition;
    Statement statement;

    public WhileStatement(SourcePosition position, AbstractExpression condition, Statement statement) {
        super(position);
        this.condition = condition;
        this.statement = statement;
    }

    public AbstractExpression getCondition() {
        return condition;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof WhileStatement ws) {
            return condition.syntaxEquals(ws.condition)
                    && statement.syntaxEquals(ws.statement);
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitWhileStatement(this);
    }
}
