package de.dercompiler.intermediate.selection;

import firm.nodes.Node;

/**
 * Represents an annotation of a {@link firm.nodes.Node}
 */
public class NodeAnnotation {
    
    private final int cost;
    // TODO: This should probably be a list
    private final Node rootNode;
    private final SubstitutionRule rule;

    /**
     * Creates a new node annotation with the given cost, root node and substitution rule
     * @param cost The cost of applying the given rule and the cheapest rules of all remaining predecessors
     * @param rootNode The bottom-most node which will be replaced
     * @param rule The substitution rule used to calculate the given cost
     */
    public NodeAnnotation(int cost, Node rootNode, SubstitutionRule rule) {
        this.cost = cost;
        this.rootNode = rootNode;
        this.rule = rule;
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
}
