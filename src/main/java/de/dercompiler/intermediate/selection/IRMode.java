package de.dercompiler.intermediate.selection;

import firm.Mode;

import static de.dercompiler.intermediate.selection.Signedness.SIGNED;
import static de.dercompiler.intermediate.selection.Signedness.UNSIGNED;

public record IRMode(Datatype type, Signedness signedness) {

    public static final IRMode PTR = new IRMode(Datatype.QWORD, UNSIGNED);
    public static final IRMode QS = new IRMode(Datatype.QWORD, SIGNED);
    public static final IRMode LU = new IRMode(Datatype.DWORD, UNSIGNED);
    public static final IRMode INT = new IRMode(Datatype.DWORD, SIGNED);
    public static final IRMode WU = new IRMode(Datatype.WORD, UNSIGNED);
    public static final IRMode WS = new IRMode(Datatype.WORD, SIGNED);
    public static final IRMode BOOL = new IRMode(Datatype.BYTE, UNSIGNED);
    public static final IRMode BS = new IRMode(Datatype.BYTE, SIGNED);
    public static final IRMode NODATA = new IRMode(Datatype.OTHER, UNSIGNED);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IRMode irMode = (IRMode) o;

        if (type != irMode.type) return false;
        return signedness == irMode.signedness;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
