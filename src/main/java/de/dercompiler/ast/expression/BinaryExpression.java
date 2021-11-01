package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public abstract sealed class BinaryExpression extends AbstractExpression permits AssignmentExpression, AddExpression, DivisionExpression, EqualExpression, GreaterEqualExpression, GreaterExpression, LessEqualExpression, LessExpression, LogicalAndExpression, LogicalOrExpression, ModuloExpression, MultiplyExpression, SubtractExpression, NotEqualExpression {

    private AbstractExpression lhs;
    private AbstractExpression rhs;

    public BinaryExpression(AbstractExpression lhs, AbstractExpression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    protected boolean syntaxEqualLhsRhs(BinaryExpression other) {
        return lhs.syntaxEqual(other.lhs) && rhs.syntaxEqual(other.rhs);
    }
}
