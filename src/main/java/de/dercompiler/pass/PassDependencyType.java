package de.dercompiler.pass;

public enum PassDependencyType {
    CLASS_PASS,
    METHOD_PASS,
    STATEMENT_PASS,
    EXPRESSION_PASS,

    //this pass type needs to run in a separate pass
    SEPARATE_PASS(true);


    private boolean naturalOrdering;

    PassDependencyType() {
        naturalOrdering = false;
    }

    PassDependencyType(boolean naturalOrder) {
        naturalOrdering = naturalOrder;
    }

    public boolean usesNaturalOrdering() { return naturalOrdering; }
}
