package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class NewObjectExpression extends PrimaryExpression {

    private CustomType type;
    public NewObjectExpression(SourcePosition position, CustomType type) {
        super(position);
        this.type = type;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NewObjectExpression noe) {
            return type.syntaxEquals(noe.type);
        }
        return false;
    }

    public CustomType getObjectType() {
        return type;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitNewObjectExpression(this);
    }
}
