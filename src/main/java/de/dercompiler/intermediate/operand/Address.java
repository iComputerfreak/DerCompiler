package de.dercompiler.intermediate.operand;

import firm.Mode;

import java.util.Objects;

public class Address implements Operand {

    // %bp or heap/object pointer
    private final X86Register base;
    private final X86Register index;
    private final int scale;
    private final int offset;
    private int components;
    private Mode mode;

    public Address(int offset, X86Register base, X86Register index, int scale) {
        this.offset = offset;
        this.base = base;
        this.index = index;
        this.scale = scale;

        this.components = this.offset > 0 ? 1 : 0;
        components = components << 1 + (Objects.isNull(this.base) ? 0 : 1);
        components = components << 1 + (Objects.isNull(this.index) ? 0 : 1);
        components = components << 1 + (this.scale > 1 ? 1 : 0);
    }

    public Address(int offset, X86Register base) {
        this(offset, base, null, 1);
    }

    @Override
    public String getIdentifier() {
        String format = switch (components) {
            case 0, 1, 8, 9 -> "%1$x";          // constant
            case 2 -> "%3$x";                   // index /w scale 1
            case 3, 11 -> "%1$d(,%3$s,%4$d)";   // index /w scale > 1
            case 4, 5 -> "%%%2$s";              // base
            case 6 -> "(%2$s,%3$s)";            // base + index
            case 7 -> "(%2$s,%3$s,%4$d)";       // base + index * scale
            case 10 -> "%1$d(,%3$s)";           // constant + index
            case 12, 13 -> "%1$d(%2$s)";        // constant + base
            case 14 -> "%1$d(%2$s,%3$s)";       // constant + base + index
            case 15 -> "%1$d(%2$s,%3$s,%4$d)";  // constant + base + index * scale
            default -> "???";
        };
        return format.formatted(offset, base, index, scale);
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Address offset(int offset) {
        return new Address(this.offset + offset, this.base, this.index, this.scale);
    }

    public Address copy() {
        return this.offset(0);
    }
}
