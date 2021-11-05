package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class ExpressionStatement extends Statement {

    AbstractExpression expressionRoot;

    public ExpressionStatement(SourcePosition position, AbstractExpression expression) {
        super(position);
        expressionRoot = expression;
    }

    public AbstractExpression getExpression() {
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
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitExpressionStatement(this);
    }
}
