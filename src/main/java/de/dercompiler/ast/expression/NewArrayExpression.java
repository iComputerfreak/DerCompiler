package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.type.BasicType;

import java.util.Objects;

public final class NewArrayExpression extends PrimaryExpression {

    private BasicType type;
    private AbstractExpression size;
    private int dimension;

    public NewArrayExpression(BasicType type, AbstractExpression size, int dimension) {
        this.type = type;
        this.size = size;
        this.dimension = dimension;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NewArrayExpression nae) {
            return type.syntaxEquals(nae.type)
                    && dimension == nae.dimension
                    && size.syntaxEquals(nae.size);
        }
        return false;
    }
}
