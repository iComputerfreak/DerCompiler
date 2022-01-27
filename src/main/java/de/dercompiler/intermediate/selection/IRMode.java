package de.dercompiler.intermediate.selection;

import firm.Mode;

import static de.dercompiler.intermediate.selection.Signedness.SIGNED;
import static de.dercompiler.intermediate.selection.Signedness.UNSIGNED;

public record IRMode(Datatype type, Signedness signedness) {
    public static IRMode forMode(Mode mode) {
        return new IRMode(Datatype.forMode(mode), mode.isSigned() ? SIGNED : UNSIGNED);
    }

    public boolean isSigned() {
        return signedness == SIGNED;
    }

    @Override
    public String toString() {
        return type.toString() + signedness.toString();
    }
}
