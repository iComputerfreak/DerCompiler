package de.dercompiler.intermediate.regalloc.analysis;

import de.dercompiler.intermediate.operand.IRRegister;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.regalloc.AccessOperations;
import de.dercompiler.intermediate.regalloc.RegAllocUtil;
import de.dercompiler.intermediate.regalloc.RegisterAllocationContext;
import de.dercompiler.intermediate.regalloc.location.Location;
import de.dercompiler.intermediate.regalloc.location.RegisterLocation;
import de.dercompiler.intermediate.regalloc.location.StackLocation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.*;

public class ShardLocationChanges {

    private final int OFFSET;
    private final int NUM_OPERATIONS;
    private final RegisterAllocationContext context;

    private record OperationState(Map<IRRegister, X86Register> scratch, Map<IRRegister, Location> longTime) {
        public OperationState() {
            this(new HashMap<>());
        }

        public OperationState(Map<IRRegister, Location> longTime) {
            this(new HashMap<>(), longTime);
        }
    }

    ArrayList<OperationState> states;

    public ShardLocationChanges(int offset, int numOperations, Map<IRRegister, Location> currentState, RegisterAllocationContext context) {
        OFFSET = offset;
        NUM_OPERATIONS = numOperations;
        states = new ArrayList<>(NUM_OPERATIONS);
        for (int i = 0; i < NUM_OPERATIONS; i++) {
            states.set(i, new OperationState(new HashMap<>(currentState)));
        }
        this.context = context;
    }

    public void apply(Map<IRRegister, Location> currentState) {

    }

    public void markAsInvalid(IRRegister register) {

    }

    public void freeRegister(IRRegister register, int opNum) {
        int effectiveNum = opNum - OFFSET;
        if (effectiveNum < 0 || effectiveNum > NUM_OPERATIONS) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("can't free register for Operation " + opNum + " this operation is not in Bounds [" + OFFSET + ", " + (OFFSET + NUM_OPERATIONS) + "]!");
        }
        for (int i = effectiveNum; i < NUM_OPERATIONS; i++) {
            states.get(i + OFFSET).longTime().remove(register);
        }
    }

    public void allocRegister(IRRegister register, Location loc, int opNum) {

    }

    public void allocScratchRegister(IRRegister register, X86Register rr, int opNum) {

    }

    public AccessOperations bindRegister(int opNum, IRRegister irr, X86Register rr) {
        //move first, because it is scratch
        AccessOperations.AccessTiming at = AccessOperations.AccessTiming.AT_TIME;

        int effectiveOpNum = opNum - OFFSET;
        if (effectiveOpNum < 0) {
            effectiveOpNum = 0;
        }

        if (!context.convention().getType(rr).isScratch()) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Binding IRRegister to non Scratch Register not possible: Try to bind " + irr + " to " + rr + "!");
        }
        Location loc = states.get(effectiveOpNum).longTime().get(irr);
        if(loc instanceof StackLocation sl) {
            return new AccessOperations(List.of(RegAllocUtil.createMoveFromStackToRegister(rr, sl)), AccessOperations.AccessTiming.END_OF_SHARD);
        } else if (loc instanceof RegisterLocation rl) {
            if (context.vlt().getLastUsage(rl.irr()) <= OFFSET + NUM_OPERATIONS) {
                freeRegister(rl.irr(), opNum + 1);
                allocScratchRegister(rl.irr(), rr, opNum + 1);
            } else {
                allocRegister(rl.irr(), new RegisterLocation(rl.irr(), rr), opNum + 1);
            }
            return new AccessOperations(List.of(RegAllocUtil.createMoveFromRegisterToRegister(rr, rl.rr())), AccessOperations.AccessTiming.AT_TIME);
        } else {
            //new
            if (context.vlt().getLastUsage(irr) <= OFFSET + NUM_OPERATIONS) {
                freeRegister(irr, opNum + 1);
                allocScratchRegister(irr, rr, opNum + 1);
            } else {
                allocRegister(irr, new RegisterLocation(irr, rr), opNum + 1);
            }
            return new AccessOperations(new LinkedList<>(), AccessOperations.AccessTiming.AT_TIME);
        }
    }


}
