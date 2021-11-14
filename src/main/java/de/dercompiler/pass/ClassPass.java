package de.dercompiler.pass;

import de.dercompiler.ast.ClassDeclaration;

public non-sealed interface ClassPass extends Pass {

    /**
     * This Method is run for each class in the Program, they may be in other order than the original parse/ast-order.
     *
     * @param classDeclaration The Class-Definition to run the Pass on.
     * @return true, if something has changed that broke a previous analysis
     */
    boolean runOnClass(ClassDeclaration classDeclaration);

    /**
     * This Method is provided to check if the Method should be run on the current class definition.
     *
     * @param classDeclaration Class-Declaration to check
     * @return true, if the Class-Declaration should be checked. There is a default implementation provided that always returns true.
     */
    default boolean checkClass(ClassDeclaration classDeclaration) { return true; }
}
