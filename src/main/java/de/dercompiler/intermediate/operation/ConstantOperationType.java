package de.dercompiler.intermediate.operation;

public enum ConstantOperationType implements OperationType {
    NO_OPERATION("", "", "");

    private final String intelSyntax;
    private final String atntSyntax;
    private final String molkiSyntax;

    ConstantOperationType(String intelSyntax, String atntSyntax, String molkiSyntax) {
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