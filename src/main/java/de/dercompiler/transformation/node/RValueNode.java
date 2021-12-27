package de.dercompiler.transformation.node;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

public class RValueNode extends ReferenceNode {

    public RValueNode(Node node, Type type) {
        super(node, type);
    }

    @Override
    public Node genLoad(TransformationState state) {
        return ref;
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("can't store value in r-value");
        return null;
    }

    @Override
    public ReferenceNode accessArray(TransformationState state, Node offset) {
        return null;
    }

    @Override
    public ReferenceNode accessField(TransformationState state) {
        return null;
    }

    @Override
    public ReferenceNode callMethod(TransformationState state) {
        return null;
    }

    @Override
    public boolean isReference() {
        return false;
    }
}
