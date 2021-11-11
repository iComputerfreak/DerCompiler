package de.dercompiler.pass;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.statement.BasicBlock;

public non-sealed interface BasicBlockPass extends Pass {

    /**
     * This Method is run for each class in the Program, they may be in other order than the original parse/ast-order.
     *
     * @param block The BasicBlock to run the Pass on.
     * @return true, if something has changed that broke a previous analysis
     */
    boolean runOnBasicBlock(BasicBlock block);

    /**
     * This Method is provided to check if the Method should be run on the current class definition.
     *
     * @param block BasicBlock to check
     * @return true, if the Class-Declaration should be checked. There is a default implementation provided that always returns true.
     */
    default boolean checkClass(BasicBlock block) { return true; }
}
