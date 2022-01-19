package de.dercompiler.intermediate.operand;

import firm.Mode;

public enum X86Register implements Operand {
    EAX("eax");
    
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
}
