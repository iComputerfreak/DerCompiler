package de.dercompiler.intermediate.memory;

import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Push;
import de.dercompiler.intermediate.regalloc.RegisterAllocator;
import firm.Entity;

import java.util.Map;
import java.util.function.Consumer;

public class BasicMemoryManager implements MemoryManager {

    /*
     *  AR layout:
     *  ,-------------,
     *  |  arg_n     | n+3     - each entry 8 bytes in size
     *  |   ...      |
     *  |  arg_1     | 4
     *  |  this      | 3
     *  |  ret_val   | 2
     *  |  dyn_link  | 1
     *  |  ret_addr  | 0     __ bp
     *  |  var_0     | -1
     *  |  ...       |
     *  |  var_m     | -m-1  __ sp
     * ´------------´
     *
     */

    /**
     *  Pointer to the return address of the current Activation Record.
     */
    private final X86Register basePointer = X86Register.RBP;

    /**
     * Pointer to the lowest entry of the current stack.
     * Also serves as variable counter: -offset = #vars.
     */
    private Address stackPointer;

    /**
     *  Maps node ids to the offset in the variable stack
     */
    private Map<Integer, Integer> variables;

    /**
     *  Accepts the operations resulting from memory operations
     */
    private Consumer<Operation> output;
    private RegisterAllocator registerMgmt;

    @Override
    public Address getVar(int n) {
        return new Address(-(n + 1) * 8, getBasePointer());
    }

    @Override
    public Operand getArgument(int n) {
        return new Address((n + 3) * 8, getBasePointer());
    }

    private X86Register getBasePointer() {
        return this.basePointer;
    }

    @Override
    public Operand getValue(int id) {
        int offset = variables.get(id);
        return basePointer.offset(offset);
    }

     @Override
    public Operand pushValue(Operand source) {
        output.accept(new Push(source));
        stackPointer = stackPointer.loadWithOffset(-8);
        return stackPointer.copy();
    }

    @Override
    public Operand getReturnValue() {
        return X86Register.RAX;
    }

    @Override
    public void enterMethod(Entity methodEntity, Operand target, Operand... arguments) {
        for (int idx = arguments.length - 1; idx >= 0; idx--) {
            pushValue(arguments[idx]);
        }
        stackPointer = stackPointer.loadWithOffset(-1);
        pushValue(basePointer);
    }

    @Override
    public void leaveMethod(Operand returnValue) {

    }

    @Override
    public void setOutput(Consumer<Operation> output) {
        this.output = output;
    }

    @Override
    public void setRegisterMgmt(RegisterAllocator registerAllocator) {
        this.registerMgmt = registerAllocator;
    }

    @Override
    public Operand getThis() {
        return getArgument(0);
    }
}
