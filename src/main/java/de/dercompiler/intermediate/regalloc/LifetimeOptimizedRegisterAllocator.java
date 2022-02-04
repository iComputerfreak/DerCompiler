package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;
import de.dercompiler.intermediate.memory.MemoryManager;
import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.regalloc.analysis.FunctionDensityOptimizer;
import de.dercompiler.intermediate.regalloc.analysis.FunctionSplitView;
import de.dercompiler.intermediate.regalloc.analysis.LifetimeAnalysis;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;
import de.dercompiler.intermediate.regalloc.analysis.VariableLifetimeTable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class LifetimeOptimizedRegisterAllocator extends RegisterAllocator {

    private final LifetimeAnalysis la;
    private final FunctionDensityOptimizer fdo;
    private static final int EXCEED_NUMBER_OF_SPILL_REGISTERS = 2;

    public LifetimeOptimizedRegisterAllocator(MemoryManager manager, CallingConvention convention) {
        super(manager, convention);
        la = new LifetimeAnalysis(convention.getNumberOfArgumentsRegisters());
        fdo = new FunctionDensityOptimizer();
    }

    public RegisterAllocationContext createContext(Function func) {
        VariableLifetimeTable vlt = la.analyze(func);
        EnumSet<X86Register> avalableRegs = EnumSet.noneOf(X86Register.class);

        avalableRegs.add(callingConvention.getReturnRegister());
        avalableRegs.addAll(List.of(callingConvention.getArgumentRegisters()));
        avalableRegs.addAll(List.of(callingConvention.getScratchRegisters()));

        if (!(vlt.getNumRegistersMaximallyActive() < callingConvention.getNumberOfScratchRegisters() + EXCEED_NUMBER_OF_SPILL_REGISTERS)) {
            avalableRegs.addAll(List.of(callingConvention.getSaveRegisters()));
        }
        FunctionSplitView fsv = fdo.analyse(func);
        fsv.calculateInformation();
        return new RegisterAllocationContext(avalableRegs, EnumSet.noneOf(X86Register.class), vlt, fsv);
    }

    @Override
    public void allocateRegisters(Function function) {
        RegisterAllocationContext context = createContext(function);
        context.splitview().print();
        ParameterRegister[] paramRegs = new ParameterRegister[function.getParamCount()];
        for (int i = 0; i < function.getParamCount(); i++) {
            paramRegs[i] = new ParameterRegister(i);
        }
        IRLocationOrganizer irloc = new IRLocationOrganizer(context.avalableRegisters(), callingConvention, paramRegs, context.vlt(), context.splitview());


    }

    @Override
    public int getVarCount() {
        return 0;
    }
}
