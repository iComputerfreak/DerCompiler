package de.dercompiler.ast.expression;

public sealed class BinaryExpression extends AbstractExpression permits AssignmentExpression, AddExpression, DivisionExpression, EqualExpression, GreaterEqualExpression, GreaterExpression, LessEqualExpression, LessExpression, LogicalAndExpression, LogicalOrExpression, ModuloExpression, MultiplyExpression, SubtractExpression, NotEqualExpression {

    private AbstractExpression lhs;
    private AbstractExpression rhs;

    public BinaryExpression(AbstractExpression lhs, AbstractExpression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
