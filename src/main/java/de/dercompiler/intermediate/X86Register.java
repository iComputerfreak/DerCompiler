package de.dercompiler.intermediate;

public enum X86Register implements Operand {
    EAX("eax");
    
    private final String identifier;
    
    X86Register(String identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public String getIdentifier() {
        return identifier;
    }
}
