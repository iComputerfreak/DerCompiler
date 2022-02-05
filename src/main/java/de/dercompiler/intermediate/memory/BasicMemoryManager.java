package de.dercompiler.intermediate.memory;

import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.RegisterAllocator;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;
import firm.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BasicMemoryManager implements MemoryManager {

    /*
     *  AR layout:
     *  ,-------------,
     *  |  arg_n     | n-4     - each entry 8 bytes in size
     *  |   ...      |
     *  |  arg_7     | 3
     *  |  arg_6     | 2
     *  |  dyn_link  | 1
     *  |  ret_addr  | 0     <- bp
     *  |  var_0     | -1
     *  |  ...       |
     *  |  var_m     | -m-1  __ sp
     * ´------------´
     *
     */

    /**
     *  Pointer to the return address of the current Activation Record.
     */
    private final X86Register basePointer;

    public BasicMemoryManager(X86Register basePointer) {
        this.basePointer = basePointer;
        stackSize = 0;
        variables = new HashMap<>();
    }

    public BasicMemoryManager() {
        this(X86Register.RBP);
    }

    /**
     *  Maps node ids to the offset in the variable stack
     */
    private Map<Operand, Integer> variables;

    /**
     *  Accepts the operations resulting from memory operations
     */
    private Consumer<Operation> output;

    private CallingConvention callingConvention;

    private int stackSize;


    @Override
    public Address getVar(int n) {
        return new Address(-(n + 1) * 8, getBasePointer());
    }

    @Override
    public Operand getArgument(int n) {
        return n < 6 ? callingConvention.getArgumentRegister(n)
                : Address.loadWithOffset(getBasePointer(), 8*(n-4));
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
       return pushValue(source, null);
    }

    @Override
    public Operand getReturnValue() {
        return X86Register.RAX;
    }

    @Override
    public void enterMethod(Entity methodEntity, Operand target, Operand... arguments) {

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
    }

    @Override
    public Operand getThis() {
        return getArgument(0);
    }

    @Override
    public void setCallingConvention(CallingConvention callingConvention) {
        this.callingConvention = callingConvention;
    }

    @Override
    public Operand pushValue(Operand value, String comment) {
        Address newTopStack = getVar(stackSize++);
        if (value == null) {
            return newTopStack;
        }
        else if (value instanceof Address) {
            X86Register temp = callingConvention.getScratchRegister(0);
            Mov push = new Mov(temp, value);
            if (comment != null) push.setComment(comment);
            output.accept(push);
            value = temp;
        }
        variables.put(value, stackSize);
        Mov mov = new Mov(newTopStack, value);
        mov.setComment("stack entry #" + stackSize);
        output.accept(mov);
        return newTopStack;
    }

    public CallingConvention getCallingConvention() {
        return callingConvention;
    }

    @Override
    public Address getStackEnd() {
        return basePointer.offset(-stackSize * 8);
    }

    @Override
    public int getStackSize() {
        return stackSize;
    }

    @Override
    public void reset() {
        this.stackSize = 0;
        variables = new HashMap<>();
    }
}
