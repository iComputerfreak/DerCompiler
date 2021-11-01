package de.dercompiler.ast.expression;

public abstract sealed class PrimaryExpression extends AbstractExpression permits NullValue, ThisValue, BooleanValue, NewArrayExpression, NewObjectExpression, IntegerValue, Variable {
}
