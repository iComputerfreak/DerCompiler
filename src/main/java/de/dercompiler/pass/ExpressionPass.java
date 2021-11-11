package de.dercompiler.pass;

import de.dercompiler.ast.expression.Expression;

public non-sealed interface ExpressionPass extends Pass {

    /**
     * This Method is run for each Expression in the Program, they may be in other order than the original parse/ast-order.
     *
     * @param expression the expression to run the pass on.
     * @return true, if something has changed that broke a previous analysis
     */
    boolean runOnExpression(Expression expression);

    /**
     * This Method is provided to check if the Method should be run on the current Expression.
     * @param expression expression to check
     * @return true, if the expression should be checked. There is a default implementation provided that always returns true.
     */
    default boolean checkExpression(Expression expression) { return true; }

    @Override
    default PassDependencyType getMinDependencyType() {
        return PassDependencyType.EXPRESSION_PASS;
    }

    @Override
    default PassDependencyType getMaxDependencyType() {
        return PassDependencyType.EXPRESSION_PASS;
    }
}
