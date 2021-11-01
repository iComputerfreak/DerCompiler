package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.type.CustomType;

import java.util.Objects;

public final class NewObjectExpression extends PrimaryExpression {

    private CustomType type;
    public NewObjectExpression(CustomType type) {
        this.type = type;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NewObjectExpression noe) {
            return type.syntaxEqual(noe.type);
        }
        return false;
    }
}
