package de.dercompiler.intermediate.operation;

public enum UnaryOperationType implements OperationType {
    CALL("", "", ""),
    DEC("", "", ""),
    DIV("", "", ""),
    MUL("", "", ""),
    JA("", "", ""),
    JAE("", "", ""),
    JB("", "", ""),
    JBE("", "", ""),
    JE("", "", ""),
    JG("", "", ""),
    JGE("", "", ""),
    JL("", "", ""),
    JLE("", "", ""),
    JMP("", "", ""),
    JNE("", "", ""),
    NEG("", "", ""),
    NOT("", "", ""),
    POP("", "", ""),
    PUSH("", "", ""),
    INCREMENT("", "", "");
    
    private final String intelSyntax;
    private final String atntSyntax;
    private final String molkiSyntax;
    
    UnaryOperationType(String intelSyntax, String atntSyntax, String molkiSyntax) {
        this.intelSyntax = intelSyntax;
        this.atntSyntax = atntSyntax;
        this.molkiSyntax = molkiSyntax;
    }

    @Override
    public String getATnT() {
        return atntSyntax;
    }

    @Override
    public String getIntel() {
        return intelSyntax;
    }

    @Override
    public String getMolki() {
        return molkiSyntax;
    }
}
