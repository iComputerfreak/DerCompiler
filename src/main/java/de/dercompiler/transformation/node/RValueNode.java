package de.dercompiler.transformation.node;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationState;
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
    public ObjectNode getObjectCallBase(TransformationState state) {
        ClassType ct = getTypeAsClass();
        return new ObjectNode(ref, ct);
    }

    @Override
    public boolean isReference() {
        return false;
    }
}
