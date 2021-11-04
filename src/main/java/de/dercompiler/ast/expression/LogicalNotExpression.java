package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class LogicalNotExpression extends UnaryExpression {

    public LogicalNotExpression(AbstractExpression encapsulated) {
        super(encapsulated);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalNotExpression lne) {
            return syntaxEqualEncapsulated(lne);
        }
        return false;
    }
}
