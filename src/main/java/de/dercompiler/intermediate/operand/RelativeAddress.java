package de.dercompiler.intermediate.operand;

import firm.Mode;

public class RelativeAddress implements Operand {

    // %bp or heap/object pointer
    private X86Register base;
    private int offset;
    private Mode mode;

    @Override
    public String getIdentifier() {
        return "%d(%s)".formatted(offset, base);
    }

    @Override
    public Mode getMode() {
        return mode;
    }
}
