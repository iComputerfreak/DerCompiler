package de.dercompiler.ast.expression;

public sealed class PrimaryExpression extends AbstractExpression permits NullValue, ThisValue, BooleanValue, NewArrayExpression, NewObjectExpression, IntegerValue, Variable {
}
