package de.dercompiler.intermediate.selection;

import firm.Mode;

public enum Datatype implements Comparable<Datatype> {
    BYTE("B", "byte"), WORD("W", "word"), DWORD("L", "dword"), QWORD("Q", "qword"), OTHER("?", "?"), NODATA("", "");

    private final String repr;
    private final String longRepr;

    Datatype(String repr, String longRepr) {
        this.repr = repr;
        this.longRepr = longRepr;
    }

    public static Datatype forMode(Mode mode) {
        return switch (mode.getSizeBytes()) {
            case 1 -> BYTE;     // Bu
            case 2 -> WORD;
            case 4 -> DWORD;    // Is
            case 8 -> QWORD;    // P, Ls
            case 0 -> NODATA;   // X
            case -1 -> OTHER;   // b
            default -> throw new IllegalStateException("Unexpected value: " + mode.getSizeBytes());
        };
    }

    @Override
    public String toString() {
        return repr;
    }

    public String getLong() {
        return longRepr;
    }
}
