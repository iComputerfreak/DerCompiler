package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class NewObjectExpression extends PrimaryExpression {

    private CustomType objType;
    public NewObjectExpression(SourcePosition position, CustomType type) {
        super(position);
        this.objType = type;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NewObjectExpression noe) {
            return objType.syntaxEquals(noe.objType);
        }
        return false;
    }

    public CustomType getObjectType() {
        return objType;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitNewObjectExpression(this);
    }
}
