package de.dercompiler.transformation;

import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.semantic.GlobalScope;
import firm.Construction;
import firm.Graph;

public class TransformationState {

    public Construction construction;
    public Graph graph;
    public final GlobalScope globalScope;

    public TransformationState(GlobalScope scope) {
        this.globalScope = scope;
        graph = null;
        construction = null;
    }

}
