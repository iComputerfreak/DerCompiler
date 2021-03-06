package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.Mode;
import firm.nodes.Node;
import firm.nodes.Proj;

import java.util.List;
import java.util.function.Function;
import java.util.stream.StreamSupport;

/**
 * Represents a rule that is used to replace one or multiple {@link firm.nodes.Node}s with a set of {@link Operation}s
 */
public abstract class SubstitutionRule<T extends Node> {
    
    protected T node;
    protected static Function<Node, NodeAnnotation<?>> annotationSupplier;
    protected IRMode mode;

    /**
     * Creates a new SubstitutionRule with the given rootNode
     */
    public SubstitutionRule() {}

    /**
     * Sets the NodeAnnotations for the root node and the other required nodes of this rule
     * @param node The NodeAnnotation of the rootNode
     *
     */
    public void setNode(T node) {
        if (this.node != null) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                    .internalError("SubstitutionRule.setNode called before clearing existing values.");
            throw new RuntimeException();
        }
        this.node = node;
    }

    /**
     * Clears existing NodeAnnotations
     */
    public void clear() {
        this.node = null;
    }

    /**
     * Returns the root node of this rule
     */
    public T getRootNode() {
        return node;
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

    protected Operand getDefinition() {
        return getAnnotation(node).getDefinition();
    }

    protected boolean hasDefinition() {
        return getDefinition() != null;
    }
    
    /**
     * Automatically sets the correct register for the given operation in this substitution rule.
     *
     * If this rule already has a valid definition, it is used. Otherwise, the definition of the operation will be used.
     * @param op The operation
     */
    protected void autosetDefinitions(Operation op) {
        if (hasDefinition()) {
            op.setDefinition(getDefinition());
        } else {
            setDefinition(op.getDefinition());
        }
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

    public List<NodeAnnotation<?>> getReplacementArgs() {
        return List.of();
    }

    /**
     * Checks whether the given input node matches this rule
     * @param inputNode The input node
     * @return Whether the rootNode of this rule matches the given input node, including their predecessors
     */
    public abstract boolean matches(T inputNode);

    protected static <N extends Node> NodeAnnotation<N> getTypedAnnotation(N node) {
        return (NodeAnnotation<N>) annotationSupplier.apply(node);
    }

    protected static NodeAnnotation<?> getAnnotation(Node node) {
        return annotationSupplier.apply(node);
    }

    protected void setDefinition(Operand target) {
        getAnnotation(getRootNode()).setDefinition(target);
    }

    public boolean needsJmpTarget() {
        return false;
    }

    public Operand createDefaultTarget() {
        return new VirtualRegister();
    }

    public Datatype getDatatype() {
        return mode != null ? mode.type() : null;
    }

    public void setMode(Mode mode) {
        Datatype datatype = Datatype.forMode(mode);
        this.mode = new IRMode(datatype, mode.isSigned() ? Signedness.SIGNED : Signedness.UNSIGNED);
    }

    public boolean isSigned() {
        return mode.isSigned();
    }

    public Signedness getSignedness() {
        return mode.signedness();
    }

    public void setMode(Datatype type, Signedness signedness) {
        this.mode = new IRMode(type, signedness);
    }

    public IRMode getMode() {
        return mode;
    }

    /**
     * Returns whether this operation depends on the current state of memory
     */
    public boolean isMemoryOperation() {
        return StreamSupport.stream(getRootNode().getPreds().spliterator(), false)
                .anyMatch(n -> n instanceof Proj p && p.getMode().equals(Mode.getM()));
    }

}

