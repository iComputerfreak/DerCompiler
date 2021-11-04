package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;
import de.dercompiler.ast.type.CustomType;

import java.util.Objects;

public final class NewObjectExpression extends PrimaryExpression {

    private CustomType type;
    public NewObjectExpression(CustomType type) {
        this.type = type;
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NewObjectExpression noe) {
            return type.syntaxEquals(noe.type);
        }
        return false;
    }
}
