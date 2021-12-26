package de.dercompiler.intermediate.operation;

public enum UnaryOperationType implements OperationType {
    CALL(""),
    DEC(""),
    DIV(""),
    MUL(""),
    JA(""),
    JAE(""),
    JB(""),
    JBE(""),
    JE(""),
    JG(""),
    JGE(""),
    JL(""),
    JLE(""),
    JMP(""),
    JNE(""),
    NEG(""),
    NOT(""),
    POP(""),
    PUSH(""),
    INC("");


    private final String syntax;

    UnaryOperationType(String syntax) {
        this.syntax = syntax;
    }

    @Override
    public String getSyntax() {
        return syntax;
    }

}
