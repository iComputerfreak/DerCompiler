package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class ModuloExpression extends BinaryExpression {

    public ModuloExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ModuloExpression me) {
            return syntaxEqualLhsRhs(me);
        }
        return false;
    }
}
