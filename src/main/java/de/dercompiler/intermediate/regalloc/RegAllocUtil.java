package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.IRRegister;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.analysis.FunctionShard;
import de.dercompiler.intermediate.regalloc.analysis.VariableLifetimeTable;
import de.dercompiler.intermediate.regalloc.location.StackLocation;

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

    public static Operation createMoveFromStackToRegister(X86Register register, StackLocation location) {
        return new Mov(register, location.address());
    }

    public static Operation createMoveFromRegisterToStack(StackLocation location, X86Register register) {
        return new Mov(location.address(), register);
    }

    public static Operation createMoveFromRegisterToRegister(X86Register to, X86Register from) {
        return new Mov(to, from);
    }

    public IRRegister findBestSpillRegister(IRRegister needed, FunctionShard shard, VariableLifetimeTable vlt) {
        int startNeeded = vlt.getDefinition(needed);
        int endNeeded = vlt.getLastUsage(needed);

        //TODO implement
        return null;
    }
}
