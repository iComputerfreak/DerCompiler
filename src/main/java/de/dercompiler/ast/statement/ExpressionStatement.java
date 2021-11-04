package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class ExpressionStatement extends Statement {

    AbstractExpression expressionRoot;

    public ExpressionStatement(SourcePosition position, AbstractExpression expression) {
        super(position);
        expressionRoot = expression;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ExpressionStatement es) {
            return expressionRoot.syntaxEquals(es.expressionRoot);
        }
        return false;
    }
}
