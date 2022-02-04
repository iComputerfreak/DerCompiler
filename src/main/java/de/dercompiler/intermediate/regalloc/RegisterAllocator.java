package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;
import de.dercompiler.intermediate.memory.MemoryManager;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;

public abstract class RegisterAllocator {

    protected final CallingConvention callingConvention;

    public RegisterAllocator(CallingConvention callingConvention) {
        this.callingConvention = callingConvention;
    }

    public abstract void allocateRegisters(Function function);
}
