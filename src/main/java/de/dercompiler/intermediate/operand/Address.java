package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.Signedness;
import firm.Mode;

import java.util.Objects;

public class Address implements Operand {

    // %bp or heap/object pointer
    private final Register base;
    private final Register index;
    private final int scale;
    private final int offset;
    private int components;

    public Address(int offset, Register base, Register index, int scale) {
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

    public Address(int offset, Register base) {
        this(offset, base, null, 1);
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
        return format.formatted(offset, base, index, scale);
    }

    public Address offset(int offset) {
        return new Address(this.offset + offset, this.base, this.index, this.scale);
    }

    public Address copy() {
        return this.offset(0);
    }

    public Address setIndex(Register index, int scale) {
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

    public boolean isRegister() {
        return switch (components) {
            case 2, 4, 5 -> true;
            default -> false;
        };
    }

    public Register asRegister() {
        return !Objects.isNull(base) ? this.base : this.index;
    }

    public static Address offset(Operand target, int offset) {
        if (target instanceof Address addr) {
            return addr.offset(offset);
        } else if (target instanceof ConstantValue constant) {
            return new Address(offset + constant.getValue(), null);
        } else if (target instanceof Register reg) {
            return new Address(offset, reg);
        }
        else throw new RuntimeException("Whoops, this operand is not supposed to be able to be offset");
    }

    public static Address ofOperand(Operand target) {
        return offset(target, 0);
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
