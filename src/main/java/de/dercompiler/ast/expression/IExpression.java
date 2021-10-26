package de.dercompiler.ast.expression;

public abstract sealed class IExpression
        permits LogicalAndExpression, LogicalOrExpression, EqualityExpression, UnequalityExpression,
                LessExpression, LessEqualExpression, GreaterExpression, GreaterEqualExpression,
                AddExpression, SubtractExpression, MultiplyExpression, DivisionExpression, ModuloExpression,
                LogicalNotExpression, NegativeExpression {

}
