package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class NegativeExpression extends UnaryExpression {

    public NegativeExpression(AbstractExpression encapsulated) {
        super(encapsulated);
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NegativeExpression ne) {
            return syntaxEqualEncapsulated(ne);
        }
        return false;
    }
}
