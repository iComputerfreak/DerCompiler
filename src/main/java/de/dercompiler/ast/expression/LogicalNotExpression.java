package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class LogicalNotExpression extends UnaryExpression {

    public LogicalNotExpression(SourcePosition position, Expression encapsulated) {
        super(position, encapsulated);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalNotExpression lne) {
            return syntaxEqualEncapsulated(lne);
        }
        return false;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitLogicalNotExpression(this);
    }
}
