package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class LocalVariableDeclarationStatement extends Statement {

    Type type;
    String identifier;
    AbstractExpression valueExpression;

    public LocalVariableDeclarationStatement(SourcePosition position, Type type, String identifier, AbstractExpression valueExpression) {
        super(position);
        this.type = type;
        this.identifier = identifier;
        this.valueExpression = valueExpression;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LocalVariableDeclarationStatement lvds) {
            return type.syntaxEquals(lvds.type)
                    && identifier.equals(lvds.identifier)
                    && valueExpression.syntaxEquals(lvds.valueExpression);
        }
        return false;
    }
}
