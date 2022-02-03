package de.dercompiler.intermediate.regalloc.calling;

import de.dercompiler.intermediate.operand.X86Register;

public class AMDSystemVCallingConvention extends CallingConvention {
    public AMDSystemVCallingConvention() {
        super(X86Register.RAX,
        /* args/save */ new X86Register[]{X86Register.RDI, X86Register.RSI, X86Register.RDX, X86Register.RCX, X86Register.R8, X86Register.R9},
        /* save */      new X86Register[]{X86Register.R10, X86Register.R11},
        /* scratch */   new X86Register[]{X86Register.RBX, X86Register.RSP, X86Register.RBP, X86Register.R12, X86Register.R13, X86Register.R14, X86Register.R15}
                );
    }
}
