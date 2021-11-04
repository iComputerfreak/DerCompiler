package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class NegativeExpression extends UnaryExpression {

    public NegativeExpression(SourcePosition position, AbstractExpression encapsulated) {
        super(position, encapsulated);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NegativeExpression ne) {
            return syntaxEqualEncapsulated(ne);
        }
        return false;
    }
}
