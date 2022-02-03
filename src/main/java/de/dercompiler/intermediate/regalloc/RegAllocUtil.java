package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.IRRegister;
import de.dercompiler.intermediate.operand.Operand;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RegAllocUtil {

    private static void collectIRVFromOP(Operand op, List<IRRegister> regs) {
        if (op instanceof IRRegister irr) {
            regs.add(irr);
        } else if (op instanceof Address address) {
            if (Objects.nonNull(address.getBase())) {
                collectIRVFromOP(address.getBase(), regs);
            }
            if (Objects.nonNull(address.getIndex())) {
                collectIRVFromOP(address.getIndex(), regs);
            }
        } else {
            //ignore
        }
    }

    public static List<IRRegister> collectIRRegisters(Operand... ops) {
        List<IRRegister> regs = new LinkedList<>();
        for (Operand op : ops) {
            collectIRVFromOP(op, regs);
        }
        return regs;
    }
}
