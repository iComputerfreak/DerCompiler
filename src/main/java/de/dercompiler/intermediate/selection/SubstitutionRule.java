package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;
import firm.Graph;
import firm.nodes.Cmp;
import firm.nodes.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a rule that is used to replace one or multiple {@link firm.nodes.Node}s with a set of {@link Operation}s
 */
public abstract class SubstitutionRule {

    // TODO: This should probably be a list
    private final Node rootNode;

    /**
     * Creates a new SubstitutionRule with the given rootNode
     * @param rootNode The root node for this rule, which is expected to have its necessary predecessors set
     */
    public SubstitutionRule(Node rootNode) {
        this.rootNode = rootNode;
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
    public int getCost() {
        return 0;
    }

    /**
     * Substitutes the given input node using this rule
     * @param node The input node
     * @return The Operation that resulted in substituting the node and its predecessors according to this rule
     */
    // Overwritten in subclass
    public Operation substitute(Node node) {
        // e.g. new BinaryOperation(ADD, graph.XXX.getLeft(), graph.XXX.getRight())
        return null;
    }

    /**
     * Returns all nodes other than the rootNode that are part of this rule, i.e. the root node's predecessors that this
     * rule uses
     * @param realGraph The Graph to extract the node references from
     * @return The {@link firm.nodes.Node} references from the given Graph that would be substituted if this rule was
     * applied
     */
    // Overwritten in subclass
    public List<Node> getRequiredNodes(Graph realGraph) {
        return new ArrayList<>();
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

