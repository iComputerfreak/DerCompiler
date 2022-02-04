package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.BinaryOperations.Sub;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Push;
import de.dercompiler.intermediate.regalloc.analysis.FunctionShard;
import de.dercompiler.intermediate.regalloc.analysis.VariableLifetimeTable;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;
import de.dercompiler.intermediate.regalloc.location.StackLocation;

import java.util.EnumSet;
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

    public static IRRegister findBestSpillRegister(IRRegister needed, FunctionShard shard, VariableLifetimeTable vlt) {
        int startNeeded = vlt.getDefinition(needed);
        int endNeeded = vlt.getLastUsage(needed);

        //TODO implement
        return null;
    }

    public static EnumSet<X86Register> findMoveRegisters(EnumSet<X86Register> used, CallingConvention convention) {
        EnumSet<X86Register> move = EnumSet.noneOf(X86Register.class);
        for (X86Register reg : convention.getSaveRegisters()) {
            if (used.contains(reg)) {
                move.add(reg);
            }
        }
        return move;
    }

    public static LinkedList<Operation> createFunctionHead(int stackSize) {
        LinkedList<Operation> head = new LinkedList<>();
        head.addLast(new Push(X86Register.RBP));
        head.addLast(new Mov(X86Register.RBP, X86Register.RSP));
        head.addLast(new Sub(new ConstantValue(stackSize), X86Register.RSP));
        return head;
    }
}
