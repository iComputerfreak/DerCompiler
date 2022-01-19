package de.dercompiler.intermediate.memory;

import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.Operation;
import jdk.incubator.foreign.ResourceScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BasicRegisterManager {

    private Map<X86Register, RegisterState> registerStates;
    private List<X86Register> freeList;
    private Consumer<Operation> output;

    public BasicRegisterManager() {
        registerStates = new HashMap<>();
        for (X86Register reg : X86Register.values()) {
            registerStates.put(reg, RegisterState.FREE);
            freeList.add(reg);
        }
    }

    public X86Register getFreeRegister() {
        X86Register register = freeList.remove(0);
        registerStates.put(register, RegisterState.OCCUPIED);
        return register;
    }

    public void releaseRegister(X86Register register) {
        freeList.add(register);
        registerStates.put(register, RegisterState.FREE);
    }

    public void setOutput(Consumer<Operation> output) {
        this.output = output;
    }

    enum RegisterState {
        FREE, OCCUPIED
    }
}
