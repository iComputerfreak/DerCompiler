package de.dercompiler.intermediate;

public class RelativeAddress implements Operand {

    // %bp or heap/object pointer
    private X86Register base;
    private int offset;

    @Override
    public String getIdentifier() {
        return "%d(%s)".formatted(offset, base);
    }
}
