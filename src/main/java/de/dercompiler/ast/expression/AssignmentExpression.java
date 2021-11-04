package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class AssignmentExpression extends BinaryExpression {

    public AssignmentExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof AssignmentExpression ae) {
            return syntaxEqualLhsRhs(ae);
        }
        return false;
    }
}
