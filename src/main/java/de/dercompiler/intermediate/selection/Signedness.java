package de.dercompiler.intermediate.selection;

public enum Signedness {
    SIGNED("S"), UNSIGNED("U");

    private final String repr;

    Signedness(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return repr;
    }
}

