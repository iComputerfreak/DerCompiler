package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Cmp;
import firm.nodes.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a rule that is used to replace one or multiple {@link firm.nodes.Node}s with a set of {@link Operation}s
 */
public abstract class SubstitutionRule<T extends Node> {

    private final T rootNode;

    /**
     * Creates a new SubstitutionRule with the given rootNode
     * @param rootNode The root node for this rule, which is expected to have its necessary predecessors set
     */
    public SubstitutionRule(T rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * Returns the root node of this rule
     */
    public T getRootNode() {
        return rootNode;
    }

    /**
     * Returns the cost of this rule
     */
    // Overwritten in subclass
    public abstract int getCost();

    /**
     * Substitutes the given input node using this rule
     * @param node The input node
     * @return The Operation that resulted in substituting the node and its predecessors according to this rule
     */
    // Overwritten in subclass
    // e.g. new BinaryOperation(ADD, graph.XXX.getLeft(), graph.XXX.getRight())
    public abstract List<Operation> substitute(Node node);

    /**
     * Checks if the type of the given node matches the generic type of this rule.
     * If the types don't match, the function throws an internal error.
     * @param node The input node
     * @return The node, cast to the type T
     */
    protected T checkNode(Node node) {
        if (!node.getClass().equals(rootNode.getClass())) {
            (new OutputMessageHandler(MessageOrigin.CODE_GENERATION))
                    .internalError("SubstitutionRule " + this.getClass().getName() + " was called with the" +
                            "wrong type of root node.\nExpected: " + rootNode.getClass().getName() +
                            "\nActual: " + node.getClass().getName());
            // We never return
            return null;
        }
        // node and rootNode have the same class; the class of rootNode is T, so casting node to T will always work.
        return (T) node;
    }

    /**
     * Returns all nodes other than the rootNode that are part of this rule, i.e. the root node's predecessors that this
     * rule uses
     * @param realGraph The Graph to extract the node references from
     * @return The {@link firm.nodes.Node} references from the given Graph that would be substituted if this rule was
     * applied
     */
    // Overwritten in subclass
    public abstract List<Node> getRequiredNodes(Graph realGraph);
    
    public boolean matches(Node inputNode) {
        return matches(rootNode, inputNode);
    }

    /**
     * Returns whether the given rule node matches the given input node
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

