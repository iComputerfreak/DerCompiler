package de.dercompiler.ast.expression;

import de.dercompiler.lexer.SourcePosition;

public sealed abstract class PostfixExpression extends UnaryExpression permits ArrayAccess, FieldAccess {
    public PostfixExpression(SourcePosition position, AbstractExpression encapsulated) {
        super(position, encapsulated);
    }
}
