package de.dercompiler.intermediate.operation;

public enum BinaryOperationType implements OperationType {
    ADD(""),
    AND(""),
    CMP(""),
    MOV(""),
    MUL(""),
    OR(""),
    ROL(""),
    ROR(""),
    SHL(""),
    SAR(""),
    SAL(""),
    SHR(""),
    SUB(""),
    XCHG(""),
    XOR(""),
    LOAD(""),
    STORE("");

    private final String syntax;

    BinaryOperationType(String syntax) {
        this.syntax = syntax;
    }

    @Override
    public String getSyntax() {
        return syntax;
    }

}
