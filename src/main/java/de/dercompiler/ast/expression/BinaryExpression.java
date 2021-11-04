package de.dercompiler.ast.expression;

import de.dercompiler.lexer.SourcePosition;

public abstract sealed class BinaryExpression extends AbstractExpression permits AssignmentExpression, AddExpression, DivisionExpression, EqualExpression, GreaterEqualExpression, GreaterExpression, LessEqualExpression, LessExpression, LogicalAndExpression, LogicalOrExpression, ModuloExpression, MultiplyExpression, SubtractExpression, NotEqualExpression {

    private AbstractExpression lhs;
    private AbstractExpression rhs;

    public BinaryExpression(SourcePosition position, AbstractExpression lhs, AbstractExpression rhs) {
        super(position);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    protected boolean syntaxEqualLhsRhs(BinaryExpression other) {
        return lhs.syntaxEquals(other.lhs) && rhs.syntaxEquals(other.rhs);
    }
}
