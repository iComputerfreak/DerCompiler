package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.generation.OperandTranslator;
import de.dercompiler.intermediate.selection.Datatype;

import java.util.Objects;

public class Address implements IRLocation {

    public static final Address NULL_PTR = new Address(0, new Address(0, null));
    // %bp or heap/object pointer
    private final Operand base;
    private final Operand index;
    private final int scale;
    private final int offset;
    private int components;

    public Address(int offset, Operand base, Operand index, int scale) {
        this.offset = offset;
        this.base = base;
        this.index = index;
        this.scale = scale;

        // which parts are actually set?
        this.components = this.offset != 0 ? 1 : 0;
        components = (components << 1) + (Objects.isNull(this.base) ? 0 : 1);
        components = (components << 1) + (Objects.isNull(this.index) ? 0 : 1);
        components = (components << 1) + (this.scale > 1 ? 1 : 0);
    }

    public Address(int offset, Operand base) {
        this(offset, base, null, 1);
    }

    public Operand getBase(){
        return base;
    }

    public Operand getIndex(){
        return index;
    }

    public Address allocate(X86Register base, X86Register index){
        return new Address(offset, base, index, scale);
    }

    @Override
    public String getIdentifier() {
        String format = switch (components) {
            case 0, 1, 8, 9 -> "0x%1$x";          // constant
            case 2 -> "%3$s";                   // index /w scale 1
            case 3, 11 -> "%1$d(,%3$s,%4$d)";   // index /w scale > 1
            case 4, 5 -> "(%2$s)";              // base
            case 6 -> "(%2$s,%3$s)";            // base + index
            case 7 -> "(%2$s,%3$s,%4$d)";       // base + index * scale
            case 10 -> "%1$d(,%3$s)";           // constant + index
            case 12, 13 -> "%1$d(%2$s)";        // constant + base
            case 14 -> "%1$d(%2$s,%3$s)";       // constant + base + index
            case 15 -> "%1$d(%2$s,%3$s,%4$d)";  // constant + base + index * scale
            default -> "???";
        };
        return format.formatted(offset, base != null ? base.getIdentifier() : null, index != null? index.getIdentifier() : null, scale);
    }

    @Override
    public String getIdentifier(Datatype datatype) {
        return getIdentifier();
    }

    @Override
    public String acceptTranslator(OperandTranslator translator, Datatype dt) {
        return translator.translate(this, dt);
    }

    public Address loadWithOffset(int offset) {
        return new Address(this.offset + offset, this.base, this.index, this.scale);
    }

    public Address copy() {
        return this.loadWithOffset(0);
    }

    public Address setIndex(Operand index, int scale) {
        // index and scale better not be set
        if ((components & 3) == 0) {
            return new Address(offset, base, index, scale);
        }
        // oh oh, we messed up
        throw new RuntimeException("Tried to overwrite index register of Address, that's a no-no");
    }

    public boolean isConstant() {
        return switch (components) {
            case 0,1,8,9 -> true;
            default -> false;
        };
    }

    public int asConstant() {
        return offset;
    }

    public static Address loadWithOffset(Operand base, int offset) {
        if (base.equals(NULL_PTR)) return NULL_PTR;
        if (base instanceof Address addr) {
            return new Address(offset, addr);
        } else if (base instanceof ConstantValue constant) {
            return new Address(offset + constant.getValue(), null);
        } else if (base instanceof Register reg) {
            return new Address(offset, reg);
        }
        else throw new RuntimeException("Whoops, this operand is not supposed to be able to be offset");
    }

    public static Address loadOperand(Operand target) {
        return loadWithOffset(target, 0);
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return scale == address.scale
                && offset == address.offset
                && Objects.equals(getBase(), address.getBase())
                && Objects.equals(getIndex(), address.getIndex());
    }

    public int getOffset() {
        return offset;
    }

    public int getScale() {
        return scale;
    }

    public Address offsetPtr(int offset) {
        return new Address(this.offset + offset, this.base, this.index, this.scale);
    }
}
