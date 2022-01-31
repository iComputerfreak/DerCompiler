package de.dercompiler.intermediate.selection;

import firm.Mode;

public enum Datatype implements Comparable<Datatype> {
    BYTE("B"), WORD("W"), DWORD("L"), QWORD("Q"), OTHER("?"), NODATA("");

    private final String repr;

    Datatype(String repr) {
        this.repr = repr;
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
}
