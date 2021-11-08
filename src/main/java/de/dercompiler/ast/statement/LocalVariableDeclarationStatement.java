package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class LocalVariableDeclarationStatement extends Statement {

    Type type;
    String identifier;
    Expression valueExpression;

    public LocalVariableDeclarationStatement(SourcePosition position, Type type, String identifier, Expression valueExpression) {
        super(position);
        this.type = type;
        this.identifier = identifier;
        this.valueExpression = valueExpression;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Expression getExpression() {
        return valueExpression;
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

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitLocalVariableDeclarationStatement(this);
    }
}
