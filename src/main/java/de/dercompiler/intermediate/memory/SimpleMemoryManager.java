package de.dercompiler.intermediate.memory;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;
import de.dercompiler.intermediate.regalloc.location.Location;
import de.dercompiler.intermediate.regalloc.location.RegisterLocation;
import de.dercompiler.intermediate.regalloc.location.StackLocation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

public class SimpleMemoryManager {

    /*
     *  Activation Record layout:
     *  ,-------------,
     *  |  arg_n     | n - k + 2 - each entry 8 bytes in size
     *  |   ...      |
     *  |  arg_k     | 2
     *  |  ret_addr  | 1
     *  |  dyn_lnk   | 0     <- bp
     *  |  var_0     | -1
     *  |  ...       |
     *  |  var_m     | -m-1  <- sp
     * ´------------´
     *
     */

    private static final int STACK_ALIGNMENT = 8;
    private CallingConvention convention;
    private int stackSize;

    public SimpleMemoryManager(CallingConvention callingConvention) {
        this.convention = callingConvention;
        stackSize = 0;
    }

    public Location getArgumentLocation(ParameterRegister pr) {
        if (pr.getId() < convention.getNumberOfArgumentsRegisters()) {
            return new RegisterLocation(pr, convention.getArgumentRegister(pr.getId()));
        }
        int numStackArgument = pr.getId() - convention.getNumberOfArgumentsRegisters() + 2;
        return new StackLocation(new Address(numStackArgument * STACK_ALIGNMENT, X86Register.RBP));
    }

    public Location getStackLocation(int numStackEntry) {
        if (numStackEntry < 0) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("invalid StackEntry for Local Variable: " + numStackEntry);
        }
        return new StackLocation(new Address(-(numStackEntry + 1) * STACK_ALIGNMENT, X86Register.RBP));
    }

    public int getNextStackLocation() {
        return stackSize++;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void resetStack() {
        stackSize = 0;
    }
}
