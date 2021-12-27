package de.dercompiler.transformation.node;

import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

public abstract class ReferenceNode {

    protected Node ref;
    protected Mode mode;
    protected Type type;

    public ReferenceNode(Node ref, Type type) {
        this.ref = ref;
        this.type = type;
        this.mode = type.getFirmType().getMode();
    }

    public abstract Node genLoad(TransformationState state);
    public abstract ReferenceNode genStore(TransformationState state, ReferenceNode value);

    public abstract ReferenceNode accessArray(TransformationState state, Node offset);
    public abstract ReferenceNode accessField(TransformationState state);
    public abstract ReferenceNode callMethod(TransformationState state);

    public abstract boolean isReference();

    public Mode getMode() {
        return type.getFirmType().getMode();
    }

    public Type getType() {
        return type;
    }
}
