package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Node;

import java.util.List;
import java.util.function.Function;

/**
 * Represents a rule that is used to replace one or multiple {@link firm.nodes.Node}s with a set of {@link Operation}s
 */
public abstract class SubstitutionRule {
    
    protected NodeAnnotation node;
    protected Function<Node, NodeAnnotation> annotationSupplier;

    /**
     * Creates a new SubstitutionRule with the given rootNode
     */
    public SubstitutionRule() {}

    /**
     * Sets the NodeAnnotations for the root node and the other required nodes of this rule
     * @param node The NodeAnnotation of the rootNode
     * @param annotationSupplier A function that transforms any of the requiredNodes into NodeAnnotations
     */
    public void setAnnotations(NodeAnnotation node, Function<Node, NodeAnnotation> annotationSupplier) {
        if (this.node != null || this.annotationSupplier != null) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                    .internalError("SubstitutionRule.setAnnotations called before clearing existing values.");
            throw new RuntimeException();
        }
        this.node = node;
        this.annotationSupplier = annotationSupplier;
    }

    /**
     * Clears existing NodeAnnotations
     */
    public void clearAnnotations() {
        this.node = null;
        this.annotationSupplier = null;
    }

    /**
     * Returns the root node of this rule
     */
    public Node getRootNode() {
        return node.getRootNode();
    }

    /**
     * Returns the cost of this rule
     */
    // Overwritten in subclass
    public abstract int getCost();

    /**
     * Substitutes the given input node using this rule
     * @return The Operation that resulted in substituting the node and its predecessors according to this rule
     */
    // Overwritten in subclass
    // e.g. new BinaryOperation(ADD, graph.XXX.getLeft(), graph.XXX.getRight())
    public abstract List<Operation> substitute();

    /**
     * Returns all nodes other than the rootNode that are part of this rule, i.e. the root node's predecessors that this
     * rule uses
     * @param realGraph The Graph to extract the node references from
     * @return The {@link firm.nodes.Node} references from the given Graph that would be substituted if this rule was
     * applied
     */
    // Overwritten in subclass
    public abstract List<Node> getRequiredNodes(Graph realGraph);

    /**
     * Checks whether the given input node matches this rule
     * @param inputNode The input node
     * @return Whether the rootNode of this rule matches the given input node, including their predecessors
     */
    public abstract boolean matches(Node inputNode);
}

