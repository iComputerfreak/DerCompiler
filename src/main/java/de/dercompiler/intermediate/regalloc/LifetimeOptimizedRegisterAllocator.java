package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;
import de.dercompiler.intermediate.memory.MemoryManager;
import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.analysis.*;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public class LifetimeOptimizedRegisterAllocator extends RegisterAllocator {

    private final LifetimeAnalysis la;
    private final FunctionDensityOptimizer fdo;
    private static final int EXCEED_NUMBER_OF_SPILL_REGISTERS = 2;
    private static final int NUM_SHARDS_TO_USE_SAVE_REGISTERS = 5;

    public LifetimeOptimizedRegisterAllocator(MemoryManager manager, CallingConvention convention) {
        super(manager, convention);
        la = new LifetimeAnalysis(convention.getNumberOfArgumentsRegisters());
        fdo = new FunctionDensityOptimizer();
    }

    public RegisterAllocationContext createContext(Function func) {
        VariableLifetimeTable vlt = la.analyze(func);
        EnumSet<X86Register> avalableRegs = EnumSet.noneOf(X86Register.class);
        EnumSet<X86Register> spareRegisters = EnumSet.noneOf(X86Register.class);

        avalableRegs.add(callingConvention.getReturnRegister());
        avalableRegs.addAll(List.of(callingConvention.getArgumentRegisters()));
        avalableRegs.addAll(List.of(callingConvention.getScratchRegisters()));

        FunctionSplitView fsv = fdo.analyse(func);
        fsv.calculateInformation();

        if (!(vlt.getNumRegistersMaximallyActive() < callingConvention.getNumberOfScratchRegisters() + EXCEED_NUMBER_OF_SPILL_REGISTERS)
            || NUM_SHARDS_TO_USE_SAVE_REGISTERS < fsv.getNumShards()) {
            avalableRegs.addAll(List.of(callingConvention.getSaveRegisters()));
        } else {
            spareRegisters.addAll(List.of(callingConvention.getSaveRegisters()));
        }
        return new RegisterAllocationContext(avalableRegs, spareRegisters, EnumSet.noneOf(X86Register.class), vlt, fsv, callingConvention);
    }

    @Override
    public void allocateRegisters(Function function) {
        RegisterAllocationContext context = createContext(function);
        context.splitview().print();
        ParameterRegister[] paramRegs = new ParameterRegister[function.getParamCount()];
        for (int i = 0; i < function.getParamCount(); i++) {
            paramRegs[i] = new ParameterRegister(i);
        }
        IRLocationOrganizer irloc = new IRLocationOrganizer(context.avalableRegisters(), callingConvention, paramRegs, context.splitview(),context);

        OperationListBuilder olb = new OperationListBuilder();

        do {
            FunctionShard shard = irloc.getShard();
            for (int i = shard.getStart(); i <= shard.getEnd(); i++) {
                Operation op = function.getOperation(i);
                AccessOperations ops = irloc.makeAvailable(op);
                if (!ops.empty()) {
                    olb.processAccessOperations(ops);
                }
                olb.appendOperation(op);
            }
            olb.finishShard();
        } while(irloc.nextSection());

        List<Operation> ops = olb.finalizeFunction();
    }

    @Override
    public int getVarCount() {
        return 0;
    }
}
