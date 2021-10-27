package de.dercompiler.ast.expression;

public final class AssignmentExpression extends BinaryExpression {
    public AssignmentExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }
}
