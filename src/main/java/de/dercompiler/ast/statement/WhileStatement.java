package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class WhileStatement extends Statement {

    Expression condition;
    Statement loop;

    public WhileStatement(SourcePosition position, Expression condition, Statement statement) {
        super(position);
        this.condition = condition;
        this.loop = statement;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getLoop() {
        return loop;
    }

    @Override
    public void markDead() {
        super.markDead();
        loop.markDead();
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof WhileStatement ws) {
            return condition.syntaxEquals(ws.condition)
                    && loop.syntaxEquals(ws.loop);
        }
        return false;
    }

    @Override
    public void accept(ASTStatementVisitor asTStatementVisitor) {
        asTStatementVisitor.visitWhileStatement(this);
    }
}
