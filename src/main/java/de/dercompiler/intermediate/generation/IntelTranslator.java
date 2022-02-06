package de.dercompiler.intermediate.generation;

import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.selection.Datatype;

import java.util.Objects;

public class IntelTranslator implements OperandTranslator {

    private static IntelTranslator translator;

    public static OperandTranslator getInstance() {
        if (translator == null) {
            translator = new IntelTranslator();
        }
        return translator;
    }

    @Override
    public String translate(Address address, Datatype dt) {
        Operand base = address.getBase();
        Operand index = address.getIndex();
        int scale = address.getScale();
        int offset = address.getOffset();
        int components = offset != 0 ? 1 : 0;
        components = (components << 1) + (Objects.isNull(base) ? 0 : 1);
        components = (components << 1) + (Objects.isNull(index) ? 0 : 1);
        components = (components << 1) + (scale > 1 ? 1 : 0);

        String format = switch (components) {
            case 0, 1, 8, 9 -> "%1$d";              // constant only
            case 2 -> "[%3$s]";                    // index register only
            case 3, 11 -> "[%3$s*%4$d + %1$d]";       // index + offset
            case 4, 5 -> "[%2$s]";                 // base only
            case 6 -> "[%2$s + %3$s]";             // base + index*1
            case 7 -> "[%2$s + %3$s*%4$d]";          // base + index*scale
            case 10 -> "[%3$s + %1$d]";             // index + offset
            case 12, 13 -> "[%2$s + %1$d]";         // base + offset
            case 14 -> "[%2$s + %3$s + %1$d]";         // base + index + offset
            case 15 -> "[%2$s + %3$s*%4$d + %1$d]";     // base + index*scale + offset
            default -> "???";
        };
        return format.formatted(offset, base != null ? base.acceptTranslator(this, dt) : null, index != null? index.acceptTranslator(this, dt) : null, scale)
                //remove + if unessessary
                .replace("+-", "-").replace("+  -", " -");
    }

    @Override
    public String translate(CondTarget target, Datatype dt) {
        return "cond ? %s : %s".formatted(target.getTrueTarget().acceptTranslator(this, dt), target.getFalseTarget().acceptTranslator(this, dt));
    }

    @Override
    public String translate(ConstantValue value, Datatype dt) {
        return "" + value.getValue();
    }

    @Override
    public String translate(LabelOperand operand, Datatype dt) {
        return "L" + operand.getTarget();
    }

    @Override
    public String translate(MethodReference mr, Datatype dt) {
        return mr.getTarget();
    }

    @Override
    public String translate(ParameterRegister pr, Datatype dt) {
        return pr.getIdentifier();
    }

    @Override
    public String translate(VirtualRegister vr, Datatype dt) {
        return vr.getIdentifier();
    }

    @Override
    public String translate(X86Register rr, Datatype dt) {
        return rr.getIdentifier(dt);
    }
}
