package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.Function;
import de.dercompiler.intermediate.memory.MemoryManager;

public abstract class RegisterAllocator {

    protected final MemoryManager manager;

    public RegisterAllocator(MemoryManager manager) {
        this.manager = manager;
    }

    public abstract void allocateRegisters(Function function);
}
