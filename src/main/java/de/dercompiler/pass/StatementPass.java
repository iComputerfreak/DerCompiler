package de.dercompiler.pass;

import de.dercompiler.ast.statement.Statement;

public non-sealed interface StatementPass extends Pass {

    /**
     * This Method is run for each Statement in the Program, they may be in other order than the original parse/ast-order.
     *
     * @param statement The Statement to run the Pass on.
     * @return true, if something has changed that broke a previous analysis
     */
    boolean runOnStatement(Statement statement);

    /**
     * This Method is provided to check if the Method should be run on the current Statement.
     *
     * @param statement Statement to check
     * @return true, if the Class-Declaration should be checked. There is a default implementation provided that always returns true.
     */
    default boolean checkStatement(Statement statement) { return true; }

    @Override
    default PassDependencyType getDependencyType() {
        return PassDependencyType.STATEMENT_PASS;
    }
}
