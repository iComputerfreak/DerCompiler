package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.type.Type;

import java.beans.Expression;
import java.util.Objects;

public final class LocalVariableDeclarationStatement extends Statement {

    Type type;
    String identifier;
    AbstractExpression valueExpression;

    public LocalVariableDeclarationStatement(Type type, String identifier, AbstractExpression valueExpression) {
        this.type = type;
        this.identifier = identifier;
        this.valueExpression = valueExpression;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LocalVariableDeclarationStatement lvds) {
            return type.syntaxEqual(lvds.type)
                    && identifier.equals(lvds.identifier)
                    && valueExpression.syntaxEqual(valueExpression);
        }
        return false;
    }
}
