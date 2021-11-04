package de.dercompiler.ast.expression;

public abstract sealed class BinaryExpression extends AbstractExpression permits AssignmentExpression, AddExpression, DivisionExpression, EqualExpression, GreaterEqualExpression, GreaterExpression, LessEqualExpression, LessExpression, LogicalAndExpression, LogicalOrExpression, ModuloExpression, MultiplyExpression, SubtractExpression, NotEqualExpression {

    private AbstractExpression lhs;
    private AbstractExpression rhs;

    public BinaryExpression(AbstractExpression lhs, AbstractExpression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    protected boolean syntaxEqualLhsRhs(BinaryExpression other) {
        return lhs.syntaxEquals(other.lhs) && rhs.syntaxEquals(other.rhs);
    }
}
