package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class WhileStatement extends Statement {

    Expression condition;
    Statement statement;

    public WhileStatement(SourcePosition position, Expression condition, Statement statement) {
        super(position);
        this.condition = condition;
        this.statement = statement;
    }

    public Expression getCondition() {
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
    public void accept(ASTStatementVisitor asTStatementVisitor) {
        asTStatementVisitor.visitWhileStatement(this);
    }
}
