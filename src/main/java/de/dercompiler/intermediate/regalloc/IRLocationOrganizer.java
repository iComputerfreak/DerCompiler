package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.memory.BasicRegisterManager;
import de.dercompiler.intermediate.memory.SimpleMemoryManager;
import de.dercompiler.intermediate.operand.IRRegister;
import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.analysis.FunctionShard;
import de.dercompiler.intermediate.regalloc.analysis.FunctionSplitView;
import de.dercompiler.intermediate.regalloc.analysis.VariableLifetimeTable;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;
import de.dercompiler.intermediate.regalloc.location.Location;

import java.util.List;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class IRLocationOrganizer {

    //list of operations of spill code, register the result is stored
    public record AccessOperations(List<Operation> operations, X86Register register) {

    }

    private final SimpleMemoryManager stack;
    private final BasicRegisterManager registers;
    private final CallingConvention convention;
    private final Map<IRRegister, Location> locationMap;
    private final VariableLifetimeTable vlt;
    private final FunctionSplitView fsv;
    private int shardID;
    private FunctionShard shard;

    public IRLocationOrganizer(EnumSet<X86Register> avalable, CallingConvention callingConvention, ParameterRegister[] params, VariableLifetimeTable varTimes, FunctionSplitView splitView) {
        stack = new SimpleMemoryManager(callingConvention);
        registers = new BasicRegisterManager(avalable);
        convention = callingConvention;
        locationMap = new HashMap<>();
        vlt = varTimes;
        fsv = splitView;
        for (ParameterRegister p : params) {
            //bind register
            locationMap.put(p, stack.getArgumentLocation(p));
        }
        shardID = 0;
        shard = fsv.getShard(0);
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
        //2. set to next shard
        shardID++;

        //3. bindNeeded values to registers
        return true;
    }

    public AccessOperations makeAvailable(IRRegister register) {
        return null;
    }

    public AccessOperations bindVirtualRegister(IRRegister register, X86Register realRegister) {
        return null;
    }

}
