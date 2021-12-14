package de.dercompiler.transformation.node;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

public class RValueNode extends ReferenceNode {

    private Mode mode;
    private Node node;

    public RValueNode(Node node, Mode mode) {
        super(node, mode);
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
    public boolean isReference() {
        return false;
    }
}
