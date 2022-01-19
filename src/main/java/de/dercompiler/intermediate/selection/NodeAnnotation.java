package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.Operand;
import firm.nodes.Node;

/**
 * Represents an annotation of a {@link firm.nodes.Node}
 */
public class NodeAnnotation {
    
    private final int cost;
    private final Node rootNode;
    private final SubstitutionRule rule;
    private Operand target = null;
    private boolean visited;
    private boolean transformed;

    /**
     * Creates a new node annotation with the given cost, root node and substitution rule
     * @param cost The cost of applying the given rule and the cheapest rules of all remaining predecessors
     * @param rootNode The bottom-most node which will be replaced
     * @param rule The substitution rule used to calculate the given cost
     * @param visited Whether the node has been visited already
     */
    public NodeAnnotation(int cost, Node rootNode, SubstitutionRule rule, boolean visited, boolean transformed) {
        this.cost = cost;
        this.rootNode = rootNode;
        this.rule = rule;
        this.visited = visited;
        this.transformed = transformed;
    }

    /**
     * Creates a new node annotation with the given cost, root node and substitution rule
     * @param cost The cost of applying the given rule and the cheapest rules of all remaining predecessors
     * @param rootNode The bottom-most node which will be replaced
     * @param rule The substitution rule used to calculate the given cost
     */
    public NodeAnnotation(int cost, Node rootNode, SubstitutionRule rule) {
        this(cost, rootNode, rule, false, false);
    }

    /**
     * Returns the cost of applying the rule to the root node, including the cost of processing all remaining
     * unprocessed predecessors
     */
    public int getCost() {
        return cost;
    }

    /**
     * The bottom-most node which will be replaced
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     * Returns the substitution rule used
     */
    public SubstitutionRule getRule() {
        return rule;
    }

    /**
     * Returns the target operand (e.g. register, virtual register, ...) of this node
     */
    public Operand getTarget() {
        return target;
    }

    /**
     * Sets the target operand (e.g. register, virtual register, ...) of this node
     * @param target The operand
     */
    public void setTarget(Operand target) {
        this.target = target;
    }

    /**
     * Returns whether this node has been visited already
     */
    public boolean getVisited() {
        return visited;
    }

    /**
     * Returns whether this node has been transformed already
     */
    public boolean getTransformed() {
        return transformed;
    }

    /**
     * Sets the visited flag for this node
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Sets the transformed flag for this node
     */
    public void setTransformed(boolean transformed) {
        this.transformed = transformed;
    }
}
