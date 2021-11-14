package de.dercompiler.pass;

import de.dercompiler.ast.ASTNode;

/**
 * @param <AST> For XXXPass the Type is XXX, as in StatementPass <AST> equal Statement
 */
public non-sealed interface AnalysisPass<AST extends ASTNode> extends Pass {

    /**
     * Returns the AnalysisResult of the Analysis for the ASTNode provided.
     *
     * @return the result for the ASTNode
     */
    AnalysisResult getAnalysisResult(AST node);
}
