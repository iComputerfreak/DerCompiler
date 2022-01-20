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
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("can't store value in r-value");
        return null;
    }

    @Override
    public ReferenceNode prepareLoad(TransformationState state) {
        prepareNode(ref, NodeAccess.LOAD);
        return this;
    }

    @Override
    public ReferenceNode prepareGetObjectCallBase(TransformationState state) {
        return new ObjectNode(ref, getTypeAsClass()).prepareGetObjectCallBase(state);
    }

    @Override
    public ObjectNode getObjectCallBase(TransformationState state) {
        return new ObjectNode(ref, getTypeAsClass());
    }

    @Override
    public boolean isReference() {
        return false;
    }
}
