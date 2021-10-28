package de.dercompiler.ast.statement;

import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.type.Type;

import java.beans.Expression;

public final class LocalVariableDeclarationStatement extends Statement {

    Type type;
    String identifier;
    AbstractExpression valueExpression;

    public LocalVariableDeclarationStatement(Type type, String identifier, AbstractExpression valueExpression) {
        this.type = type;
        this.identifier = identifier;
        this.valueExpression = valueExpression;
    }

    public LocalVariableDeclarationStatement(Type type, String identifier) {
        this(type, identifier, null);
    }
}
