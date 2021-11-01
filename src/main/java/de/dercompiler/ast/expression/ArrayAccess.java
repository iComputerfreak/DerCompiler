package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class ArrayAccess extends PostfixExpression {

    private AbstractExpression arrayPosition;

    public ArrayAccess(AbstractExpression encapsulated, AbstractExpression arrayPosition) {
        super(encapsulated);
        this.arrayPosition = arrayPosition;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ArrayAccess aa) {
            return arrayPosition.syntaxEqual(aa.arrayPosition) && syntaxEqualEncapsulated(aa);
        }
        return false;
    }
}
