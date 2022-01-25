package de.dercompiler.intermediate.selection;

import firm.Mode;

public enum Datatype implements Comparable<Datatype> {
    BYTE("B"), WORD("W"), DWORD("D"), QWORD("Q"), OTHER("?");

    private final String repr;

    Datatype(String repr) {
        this.repr = repr;
    }

    public static Datatype forMode(Mode mode) {
        return switch (mode.getSizeBytes()) {
            case 1 -> BYTE;
            case 2 -> WORD;
            case 4 -> DWORD;
            case 8 -> QWORD;
            case 0, -1 -> OTHER;
            default -> throw new IllegalStateException("Unexpected value: " + mode.getSizeBytes());
        };
    }

    @Override
    public String toString() {
        return repr;
    }
}
