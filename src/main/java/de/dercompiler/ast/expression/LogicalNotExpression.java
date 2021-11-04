package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class LogicalNotExpression extends UnaryExpression {

    public LogicalNotExpression(AbstractExpression encapsulated) {
        super(encapsulated);
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalNotExpression lne) {
            return syntaxEqualEncapsulated(lne);
        }
        return false;
    }
}
