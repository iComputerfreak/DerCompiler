package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class ArrayAccess extends PostfixExpression {

    private AbstractExpression arrayPosition;

    public ArrayAccess(AbstractExpression encapsulated, AbstractExpression arrayPosition) {
        super(encapsulated);
        this.arrayPosition = arrayPosition;
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ArrayAccess aa) {
            return arrayPosition.syntaxEquals(aa.arrayPosition) && syntaxEqualEncapsulated(aa);
        }
        return false;
    }
}
