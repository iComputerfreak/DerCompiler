package de.dercompiler.intermediate.memory;

import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.Operation;

import java.util.*;
import java.util.function.Consumer;

public class BasicRegisterManager {

    private final EnumMap<X86Register, RegisterState> registerStates;
    private final List<X86Register> freeList;

    public BasicRegisterManager(EnumSet<X86Register> available) {
        registerStates = new EnumMap<X86Register, RegisterState>(X86Register.class);
        freeList = new LinkedList<>();
        for (X86Register reg : available) {
            registerStates.put(reg, RegisterState.FREE);
            freeList.add(reg);
        }
    }

    public X86Register allocateRegister() {
        X86Register register = freeList.remove(0);
        registerStates.put(register, RegisterState.OCCUPIED);
        return register;
    }

    public boolean allocateRegister(X86Register register) {
        if (!checkIfRegisterIsFree(register)) return false;
        freeList.remove(register);
        registerStates.put(register, RegisterState.OCCUPIED);
        return true;
    }

    public void freeRegister(X86Register register) {
        freeList.add(register);
        registerStates.put(register, RegisterState.FREE);
    }

    public boolean checkIfRegisterIsFree(X86Register reg) {
        return registerStates.get(reg) == RegisterState.FREE;
    }

    enum RegisterState {
        FREE, OCCUPIED
    }
}
