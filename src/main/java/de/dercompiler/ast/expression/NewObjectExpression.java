package de.dercompiler.ast.expression;

import de.dercompiler.ast.type.CustomType;

public final class NewObjectExpression extends PrimaryExpression {

    private CustomType type;
    public NewObjectExpression(CustomType type) {
        this.type = type;
    }
}
