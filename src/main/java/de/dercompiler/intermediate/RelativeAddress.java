package de.dercompiler.intermediate;

public class RelativeAddress implements Operand {

    private X86Register base; //%bp or
    private int offset;

    @Override
    public String getIdentifier() {
        return "%d(%s)".formatted(offset, base);
    }
}
