package de.dercompiler.transformation.node;

import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

public abstract class ReferenceNode {

    protected Node ref;
    protected Mode mode;

    public ReferenceNode(Node ref, Mode mode) {
        this.ref = ref;
        this.mode = mode;
    }

    public abstract Node genLoad(TransformationState state);
    public abstract ReferenceNode genStore(TransformationState state, ReferenceNode value);

    public abstract boolean isReference();

    public Mode getMode() {
        return mode;
    }
}
