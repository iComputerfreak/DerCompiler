package de.dercompiler.ast.expression;

import de.dercompiler.ast.type.BasicType;

public final class NewArrayExpression extends PrimaryExpression {

    private BasicType type;
    int dimension;

    public NewArrayExpression(BasicType type, int dimension) {
        this.type = type;
        this.dimension = dimension;
    }
}
