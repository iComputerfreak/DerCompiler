package de.dercompiler.ast.expression;

public sealed class PrimaryExpression extends AbstractExpression permits NullValue, TrueValue, FalseValue, IntegerValue, Variable {
}
