package de.dercompiler.intermediate.operation;

public enum ConstantOperationType implements OperationType {
    NOP(""),
    RET("");

    private final String syntax;

    ConstantOperationType(String syntax) {
        this.syntax = syntax;
    }

    @Override
    public String getSyntax() {
        return syntax;
    }

}
