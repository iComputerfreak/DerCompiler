package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class FieldAccess extends PostfixExpression {

    String fieldName;
    public FieldAccess(AbstractExpression encapsulated, String fieldName) {
        super(encapsulated);
        this.fieldName = fieldName;
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof FieldAccess fa) {
            return fieldName.equals(fa.fieldName) && syntaxEqualEncapsulated(fa);
        }
        return false;
    }
}
