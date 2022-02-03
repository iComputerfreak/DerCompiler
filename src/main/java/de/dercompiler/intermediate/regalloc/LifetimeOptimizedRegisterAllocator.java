package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;
import de.dercompiler.intermediate.memory.MemoryManager;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;

public class LifetimeOptimizedRegisterAllocator extends RegisterAllocator {

    private final LifetimeAnalysis la;

    public LifetimeOptimizedRegisterAllocator(MemoryManager manager, CallingConvention convention) {
        super(manager, convention);
        la = new LifetimeAnalysis(convention.getNumberOfArgumentsRegisters());
    }

    @Override
    public void allocateRegisters(Function function) {
        VariableLifetimeTable vlt = la.analyze(function);

    }
}
