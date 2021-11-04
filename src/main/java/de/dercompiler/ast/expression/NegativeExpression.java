package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class NegativeExpression extends UnaryExpression {

    public NegativeExpression(AbstractExpression encapsulated) {
        super(encapsulated);
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NegativeExpression ne) {
            return syntaxEqualEncapsulated(ne);
        }
        return false;
    }
}
