package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.Type;

import java.util.Objects;

public final class NewArrayExpression extends PrimaryExpression {

    private BasicType type;
    int dimension;

    public NewArrayExpression(BasicType type, int dimension) {
        this.type = type;
        this.dimension = dimension;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NewArrayExpression nae) {
            return type.syntaxEqual(nae.type)
                    && dimension == nae.dimension;
        }
        return false;
    }
}
