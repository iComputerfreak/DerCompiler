package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

public abstract sealed class AbstractExpression implements ASTNode permits BinaryExpression, ErrorExpression, PrimaryExpression, UnaryExpression, UninitializedValue, VoidExpression {

}
