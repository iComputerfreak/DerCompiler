package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class ReturnStatement extends Statement {

    Expression returnExpression;

    public ReturnStatement(SourcePosition position, Expression returnExpression) {
        super(position);
        this.returnExpression = returnExpression;
    }

    public Expression getExpression() {
        return returnExpression;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ReturnStatement rs) {
            return returnExpression.syntaxEquals(rs.returnExpression);
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitReturnStatement(this);
    }
}
