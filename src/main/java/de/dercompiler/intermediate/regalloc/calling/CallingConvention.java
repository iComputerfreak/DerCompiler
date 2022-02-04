package de.dercompiler.intermediate.regalloc.calling;

import de.dercompiler.intermediate.CodeGenerationWarningIds;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.EnumSet;

public class CallingConvention {
    /*
     * caller:
     *  ...
     *  asm
     *  ...
     *  call callee
     *  ...
     */

    private X86Register returnRegister;
    private X86Register[] argumentRegister;
    private X86Register[] saveRegisters;
    private X86Register[] scratchRegisters;

    public enum RegisterType {
        RETURN_REGISTER(true),
        ARGUMENT_REGISTER(true),
        SCRAP_REGISTER(true),
        SAVE_REGISTER(false);

        private boolean scratch;
        RegisterType(boolean scratch) {
            this.scratch = scratch;
        }

        public boolean isScratch() {
            return scratch;
        }
    }

    public CallingConvention(X86Register returnRegister, X86Register[] argumentRegister, X86Register[] scratchRegister, X86Register[] saveRegisters) {
        this.returnRegister = returnRegister;
        this.argumentRegister = argumentRegister;
        this.saveRegisters = saveRegisters;
        this.scratchRegisters = scratchRegister;

        EnumSet<X86Register> set = EnumSet.of(returnRegister);

        //argument registers are by definition scratch registers
        for (X86Register r : argumentRegister) {
            if (set.contains(r)) {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Invalid CallingConvention, duplicate Register in: ArgumentRegister");
            }
            set.add(r);
        }

        for (X86Register r : saveRegisters) {
            if (set.contains(r)) {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Invalid CallingConvention, duplicate Register in: SaveRegister");
            }
            set.add(r);
        }

        for (X86Register r : scratchRegister) {
            if (set.contains(r)) {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Invalid CallingConvention, duplicate Register in: ScratchRegister");
            }
            set.add(r);
        }
        if (set.size() != X86Register.values().length) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printWarning(CodeGenerationWarningIds.NOT_ALL_REGISTERS_USED_IN_CALLING_CONVENTION, "Not all X86Registers used in CallingConvention!");
        }
    }

    public int getNumberOfArgumentsRegisters() {
        return argumentRegister.length;
    }

    public int getNumberOfSaveRegisters() {
        return saveRegisters.length;
    }

    public int getNumberOfScratchRegisters() {
        return scratchRegisters.length;
    }

    public X86Register getReturnRegister() {
        return returnRegister;
    }

    public X86Register getArgumentRegister(int idx) {
        validateAccess(idx, getNumberOfArgumentsRegisters(), "ArgumentRegister");
        return argumentRegister[idx];
    }

    public X86Register getScratchRegister(int idx) {
        validateAccess(idx, getNumberOfScratchRegisters(), "ScratchRegister");
        return scratchRegisters[idx];
    }

    public X86Register getSaveRegister(int idx) {
        validateAccess(idx, getNumberOfSaveRegisters(), "SaveRegister");
        return saveRegisters[idx];
    }

    public X86Register[] getArgumentRegisters() {
        return argumentRegister;
    }

    public X86Register[] getScratchRegisters() {
        return scratchRegisters;
    }

    public X86Register[] getSaveRegisters() {
        return saveRegisters;
    }

    private void validateAccess(int idx, int maxIndex, String registerType) {
        if (idx < 0 || maxIndex <= idx) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Accessed " + registerType + " is only valid in range index 0 <= x < " + maxIndex + ", but index was: " + idx +"!");
        }
    }

    public RegisterType getType(X86Register rr) {
        if (rr == returnRegister) {
            return RegisterType.RETURN_REGISTER;
        }
        if (ofRegisterType(argumentRegister, rr)) {
            return RegisterType.ARGUMENT_REGISTER;
        }
        if (ofRegisterType(scratchRegisters, rr)) {
            return RegisterType.SCRAP_REGISTER;
        }
        return RegisterType.SAVE_REGISTER;
    }

    private boolean ofRegisterType(X86Register[] registerType, X86Register rr) {
        for (X86Register reg : registerType) {
            if (reg == rr) return true;
        }
        return false;
    }
}
