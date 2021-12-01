package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class ExpressionStatement extends Statement {

    Expression expressionRoot;

    public ExpressionStatement(SourcePosition position, Expression expression) {
        super(position);
        expressionRoot = expression;
    }

    public Expression getExpression() {
        return expressionRoot;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ExpressionStatement es) {
            return expressionRoot.syntaxEquals(es.expressionRoot);
        }
        return false;
    }

    @Override
    public void accept(ASTStatementVisitor asTStatementVisitor) {
        asTStatementVisitor.visitExpressionStatement(this);
    }
}
