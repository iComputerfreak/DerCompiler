package de.dercompiler.linker;

public enum AssemblerStyle {
    ATAndT,
    Intel;

    AssemblerStyle() {}

    public boolean isIntel() {
        return this == Intel;
    }

    public boolean isAtAndT() {
        return this == ATAndT;
    }
}
