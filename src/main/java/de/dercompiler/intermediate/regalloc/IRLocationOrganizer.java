package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.memory.BasicRegisterManager;
import de.dercompiler.intermediate.memory.SimpleMemoryManager;
import de.dercompiler.intermediate.operand.IRRegister;
import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.NaryOperations.Call;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.analysis.FunctionShard;
import de.dercompiler.intermediate.regalloc.analysis.FunctionSplitView;
import de.dercompiler.intermediate.regalloc.analysis.ShardLocationChanges;
import de.dercompiler.intermediate.regalloc.analysis.VariableLifetimeTable;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;
import de.dercompiler.intermediate.regalloc.location.Location;
import de.dercompiler.intermediate.regalloc.location.StackLocation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.List;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class IRLocationOrganizer {

    private final SimpleMemoryManager stack;
    private final BasicRegisterManager registers;
    private final CallingConvention convention;
    private final Map<IRRegister, Location> locationMap;
    private final FunctionSplitView fsv;
    private int shardID;
    private FunctionShard shard;
    private ShardLocationChanges slc;
    private RegisterAllocationContext context;

    public IRLocationOrganizer(EnumSet<X86Register> avalable, CallingConvention callingConvention, ParameterRegister[] params, FunctionSplitView splitView, RegisterAllocationContext context) {
        stack = new SimpleMemoryManager(callingConvention);
        registers = new BasicRegisterManager(avalable);
        convention = callingConvention;
        locationMap = new HashMap<>();
        fsv = splitView;
        for (ParameterRegister p : params) {
            //bind register
            locationMap.put(p, stack.getArgumentLocation(p));
        }
        shardID = 0;
        shard = fsv.getShard(0);
        slc = new ShardLocationChanges(shard.getStart(), shard.getNumOperations(), locationMap, context);
        this.context = context;
    }

    public boolean nextSection() {
        if (!(shardID < fsv.getNumShards())) return false;
        //1. scratch registers

        for (X86Register reg : convention.getArgumentRegisters()) {
            registers.freeRegister(reg);
        }
        for (X86Register reg : convention.getScratchRegisters()) {
            registers.freeRegister(reg);
        }

        Operation op = fsv.getFunction().getOperations().get(shard.getEnd());
        //free return register or bind ne definition
        registers.freeRegister(convention.getReturnRegister());
        if (op.hasDefinition()) {
            if (op.getDefinition() instanceof VirtualRegister vr) {
                bindVirtualRegister(vr, convention.getReturnRegister());
            } else {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("ReturnValue should always be a VirtualRegister, bot got: " + op.getDefinition().getClass().toString());
            }
        }

        //2. set to next shard
        shardID++;
        context.spillRegisters().addAll(slc.getPossibleSpillRegisters());
        slc.apply(locationMap);
        shard = fsv.getShard(shardID);
        slc = new ShardLocationChanges(shard.getStart(), shard.getNumOperations(), locationMap, context);
        //3. bindNeeded values to registers

        return true;
    }

    public FunctionShard getShard() {
        return shard;
    }

    public AccessOperations makeAvailable(Operation op) {

        if (op instanceof Call call) {

        }

        List<IRRegister> needed = RegAllocUtil.collectIRRegisters(op.getArgs());


        return null;
    }

    public AccessOperations bindVirtualRegister(IRRegister register, X86Register realRegister) {
        //handle StackRegister
        if (register instanceof ParameterRegister pr && pr.getId() >= convention.getNumberOfArgumentsRegisters()) {
            Location loc = stack.getArgumentLocation(pr);
            if (loc instanceof StackLocation location) {
                return new AccessOperations(List.of(new Mov(realRegister, location.address())), AccessOperations.AccessTiming.END_OF_SHARD);
            } else {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Invalid Location for StackParameter!");
                return null; //we never return
            }
        }
        //handle Everything else
        int opNum = context.vlt().getDefinition(register);
        return slc.bindRegister(opNum, register, realRegister);
    }

}
