package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;
import de.dercompiler.intermediate.memory.MemoryManager;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;

public class LifetimeOptimizedRegisterAllocator extends RegisterAllocator {

    private static final LifetimeAnalysis la = new LifetimeAnalysis(6);

    public LifetimeOptimizedRegisterAllocator(MemoryManager manager, CallingConvention convention) {
        super(manager, convention);
    }

    @Override
    public void allocateRegisters(Function function) {

    }
}
