package de.dercompiler.intermediate.selection;

import static de.dercompiler.intermediate.selection.Signedness.SIGNED;

public record IRMode(Datatype type, Signedness signedness) {
    public boolean isSigned() {
        return signedness == SIGNED;
    }

    @Override
    public String toString() {
        return type.toString() + signedness.toString();
    }
}
