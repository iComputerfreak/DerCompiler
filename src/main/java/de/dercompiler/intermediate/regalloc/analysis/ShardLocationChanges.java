package de.dercompiler.intermediate.regalloc.analysis;

import de.dercompiler.intermediate.operand.IRRegister;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.regalloc.AccessOperations;
import de.dercompiler.intermediate.regalloc.RegAllocUtil;
import de.dercompiler.intermediate.regalloc.RegisterAllocationContext;
import de.dercompiler.intermediate.regalloc.location.IRRegisterBinding;
import de.dercompiler.intermediate.regalloc.location.Location;
import de.dercompiler.intermediate.regalloc.location.RegisterLocation;
import de.dercompiler.intermediate.regalloc.location.StackLocation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.*;

import static java.lang.Math.min;

public class ShardLocationChanges {

    private final int OFFSET;
    private final int NUM_OPERATIONS;
    private final RegisterAllocationContext context;

    ArrayList<IRRegisterBinding> states;

    public ShardLocationChanges(int offset, int numOperations, Map<IRRegister, Location> currentState, RegisterAllocationContext context) {
        OFFSET = offset;
        NUM_OPERATIONS = numOperations;
        states = new ArrayList<>(NUM_OPERATIONS);
        for (int i = 0; i < NUM_OPERATIONS; i++) {
            states.set(i, new IRRegisterBinding(currentState, context.avalableRegisters(), context.spareRegisters()));
        }
        this.context = context;
    }

    public void apply(Map<IRRegister, Location> currentState) {
       states.get(states.size() - 1).apply(currentState);
    }

    public void freeRegister(IRRegister register, int opNum) {
        int effectiveNum = opNum - OFFSET;
        if (effectiveNum < 0 || effectiveNum > NUM_OPERATIONS) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("can't free register for Operation " + opNum + " this operation is not in Bounds [" + OFFSET + ", " + (OFFSET + NUM_OPERATIONS) + "]!");
        }
        for (int i = effectiveNum; i < NUM_OPERATIONS; i++) {
            states.get(i).unbindRegister(register);
        }
    }

    public X86Register allocRegister(IRRegister reg, int opNum, int end) {
        FunctionShard shard = context.splitview().getShardOfOperation(opNum);
        //TODO: implement
        if (end > shard.getEnd()) {
            //long living value

        } else {

        }
        return null;
    }

    public void allocRegister(RegisterLocation loc, int opNum, int end) {
        int effectiveOpNum = opNum - OFFSET;
        if (effectiveOpNum < 0) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("IRRegister doesn't start in this shard");
        }
        FunctionShard shard = context.splitview().getShardOfOperation(opNum);
        int effektiveEnd = min(end, shard.getEnd());
        for (int i = effectiveOpNum; i < effektiveEnd; i++) {
            states.get(i).bindRegister(loc);
        }
    }

    public AccessOperations bindRegister(int opNum, IRRegister irr, X86Register rr) {
        //move first, because it is scratch
        int effectiveOpNum = opNum - OFFSET;
        if (effectiveOpNum < 0) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("IRRegister doesn't start in this shard");
        }

        int end = context.vlt().getLastUsage(irr);

        if (!context.convention().getType(rr).isScratch()) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Binding IRRegister to non Scratch Register not possible: Try to bind " + irr + " to " + rr + "!");
        }
        Location loc = states.get(effectiveOpNum).findLocation(irr);
        if(loc instanceof StackLocation sl) {
            return new AccessOperations(List.of(RegAllocUtil.createMoveFromStackToRegister(rr, sl)), AccessOperations.AccessTiming.END_OF_SHARD);
        } else if (loc instanceof RegisterLocation rl) {
            if (context.vlt().getLastUsage(rl.irr()) <= OFFSET + NUM_OPERATIONS) {
                freeRegister(rl.irr(), opNum + 1);
                allocRegister(new RegisterLocation(rl.irr(), rr), opNum + 1, end);
            } else {
                allocRegister(new RegisterLocation(rl.irr(), rr), opNum + 1, end);
            }
            return new AccessOperations(List.of(RegAllocUtil.createMoveFromRegisterToRegister(rr, rl.rr())), AccessOperations.AccessTiming.AT_TIME);
        } else {
            //new
            if (context.vlt().getLastUsage(irr) <= OFFSET + NUM_OPERATIONS) {
                freeRegister(irr, opNum + 1);
                allocRegister(new RegisterLocation(irr, rr), opNum + 1, end);
            } else {
                allocRegister(new RegisterLocation(irr, rr), opNum + 1, end);
            }
            return new AccessOperations(new LinkedList<>(), AccessOperations.AccessTiming.AT_TIME);
        }
    }

    public EnumSet<X86Register> getPossibleSpillRegisters() {
        EnumSet<X86Register> spill = EnumSet.noneOf(X86Register.class);
        for (IRRegisterBinding bind : states) {
            spill.addAll(bind.getUsage());
        }
        return spill;
    }

}
