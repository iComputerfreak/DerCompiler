package de.dercompiler.pass;

import de.dercompiler.ast.expression.Expression;
import de.dercompiler.io.message.IErrorIds;

public enum PassErrorIds implements IErrorIds {

    UNKNOWN_EXPRESSION(700),
    TYPE_MISMATCH(701),
    UNDEFINED_VARIABLE(702),
    UNKNOWN_METHOD(703),
    ILLEGAL_METHOD_CALL(704),
    UNKNOWN_FIELD(705),
    ILLEGAL_FIELD_REFERENCE(706),
    ARGUMENTS_MISMATCH(707),
    DUPLICATE_CLASS(708),
    SPECS_VIOLATION(709), ILLEGAL_ARRAY_ACCESS(710), ILLEGAL_ASSIGNMENT(711), UNKNOWN_TYPE(712), DUPLICATE_VARIABLE(713), DUPLICATE_FIELD(714), DUPLICATE_METHOD(715), ILLEGAL_FIELD_TYPE(716), ILLEGAL_PARAMETER_TYPE(717);
    private int id;

    PassErrorIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
