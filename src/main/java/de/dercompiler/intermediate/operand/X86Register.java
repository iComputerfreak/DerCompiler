package de.dercompiler.intermediate.operand;

import firm.Mode;

public enum X86Register implements Operand {
    EAX("eax"),
    ECX("ecx"),
    EDX("edx"),
    EBX("ebx"),
    RSP("rsp"),
    RBP("rbp"),
    RSI("rsi"),
    RDI("rdi"),
    R8D("r8d"),
    R9D("r9d"),
    R10D("r10d"),
    R11D("r11d"),
    R12D("r12d"),
    R13D("r13d"),
    R14D("r14d"),
    R15D("r15d");

    private final String identifier;
    private Mode mode;

    X86Register(String identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Operand offset(int offset) {
        return new Address(offset, this);
    }
}
