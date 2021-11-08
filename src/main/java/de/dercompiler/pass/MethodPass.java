package de.dercompiler.pass;

import de.dercompiler.ast.Method;

public non-sealed interface MethodPass extends Pass {

    /**
     * This Method is run for each Method in the Program, they may be in other order than the original parse/ast-order.
     *
     * @param method The Method to run the Pass on.
     * @return true, if something has changed that broke a previous analysis
     */
    boolean runOnMethod(Method method);

    /**
     * This Method is provided to check if the Method should be run on the Method handed over.
     *
     * @param method Method to check
     * @return true, if the Class-Declaration should be checked. There is a default implementation provided that always returns true.
     */
    default boolean checkMethod(Method method) { return true; }

}
