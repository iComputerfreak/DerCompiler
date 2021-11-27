package de.dercompiler.transformation;

import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.semantic.GlobalScope;
import firm.Construction;
import firm.Graph;
import firm.nodes.Node;

import java.util.Stack;

public class TransformationState {

    public Construction construction;
    public Graph graph;
    public final GlobalScope globalScope;
    public Node lhs;
    public Node rhs;
    public Node res;

    public TransformationState(GlobalScope scope) {
        this.globalScope = scope;
        graph = null;
        construction = null;
    }

}
