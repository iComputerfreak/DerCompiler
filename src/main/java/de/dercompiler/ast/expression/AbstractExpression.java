package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

public abstract sealed class AbstractExpression permits BinaryExpression, PrimaryExpression, UnaryExpression {

}
