package de.dercompiler.intermediate.operation;

public enum BinaryOperationType implements OperationType {
    ADD("", "", ""),
    AND("", "", ""),
    CMP("", "", ""),
    MOV("", "", ""),
    MUL("", "", ""),
    OR("", "", ""),
    ROL("", "", ""),
    ROR("", "", ""),
    SHL("", "", ""),
    SAR("", "", ""),
    SHR("", "", ""),
    SUB("", "", ""),
    XCHG("", "", ""),
    XOR("", "", "");

    private final String intelSyntax;
    private final String atntSyntax;
    private final String molkiSyntax;

    BinaryOperationType(String intelSyntax, String atntSyntax, String molkiSyntax) {
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
