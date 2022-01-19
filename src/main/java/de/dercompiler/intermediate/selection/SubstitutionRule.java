package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;
import firm.Graph;
import firm.nodes.Cmp;
import firm.nodes.Node;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Represents a rule that is used to replace one or multiple {@link firm.nodes.Node}s with a set of {@link Operation}s
 */
public abstract class SubstitutionRule {

    private final Node rootNode;
    protected NodeAnnotation node;
    protected Function<Node, NodeAnnotation> annotationSupplier;

    /**
     * Creates a new SubstitutionRule with the given rootNode
     * @param rootNode The root node for this rule, which is expected to have its necessary predecessors set
     */
    public SubstitutionRule(Node rootNode) {
        this.rootNode = rootNode;
    }
    
    // TODO: Use and add docs
    public void setAnnotations(NodeAnnotation node, Function<Node, NodeAnnotation> annotationSupplier) {
        this.node = node;
        this.annotationSupplier = annotationSupplier;
    }
    
    public void clearAnnotations() {
        this.node = null;
        this.annotationSupplier = null;
    }

    /**
     * Returns the root node of this rule
     */
    public Node getRootNode() {
        return rootNode;
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
    
    // TODO: Doc
    public boolean matches(Node inputNode) {
        return matches(getRootNode(), inputNode);
    }

    /**
     * Returns whether the given rule matches the given input node
     * @param ruleNode The rootNode of the rule
     * @param inputNode The input node that is expected to match the rootNode
     * @return Whether the given rootNode matches the given input node, including their predecessors
     */
    public static boolean matches(Node ruleNode, Node inputNode) {
        // The root node's type has to match
        if (!(inputNode.getClass().equals(ruleNode.getClass()))) {
            return false;
        }

        // If the node is a CmpNode, its relation has to match
        if (ruleNode instanceof Cmp c1 &&
                inputNode instanceof Cmp c2 &&
                c1.getRelation() != c2.getRelation()) {
            return false;
        }

        // The number of predecessors has to match
        if (ruleNode.getPredCount() != inputNode.getPredCount()) {
            return false;
        }
        // Each predecessor has to match
        Iterator<Node> ruleIterator = ruleNode.getPreds().iterator();
        Iterator<Node> inputIterator = inputNode.getPreds().iterator();
        while (ruleIterator.hasNext()) {
            Node nextRoot = ruleIterator.next();
            Node nextInput = inputIterator.next();
            if (!matches(nextRoot, nextInput)) {
                return false;
            }
        }
        return true;
    }
}

