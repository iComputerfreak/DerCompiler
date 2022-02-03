package de.dercompiler.intermediate.memory;

import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.Operation;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BasicRegisterManager {

    private EnumMap<X86Register, RegisterState> registerStates;
    private List<X86Register> freeList;

    public BasicRegisterManager() {
        registerStates = new EnumMap<X86Register, RegisterState>(X86Register.class);
        for (X86Register reg : X86Register.values()) {
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
