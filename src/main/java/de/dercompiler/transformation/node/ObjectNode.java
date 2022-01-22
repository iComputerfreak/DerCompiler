package de.dercompiler.transformation.node;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.util.Utils;
import firm.Entity;
import firm.nodes.Node;

public class ObjectNode extends ReferenceNode{

    public ObjectNode(Node ref, Type type) {
        super(ref, type);
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        new OutputMessageHandler(MessageOrigin.TRANSFORM).internalNestedError("no store on classes allowed, we only work on references", 1);
        return value;
    }

    @Override
    public ReferenceNode prepareLoad(TransformationState state) {
        //load returns the reference if we wan't to store it
        prepareNode(ref, NodeAccess.LOAD);
        return this;
    }

    @Override
    public ReferenceNode accessArray(TransformationState state, Node offset) {
        new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("tried to call ArrayAccess on Object");
        return null; //we never return
    }

    @Override
    public ReferenceNode prepareGetObjectCallBase(TransformationState state) {
        return this;
    }

    @Override
    public ObjectNode getObjectCallBase(TransformationState state) {
        return this;
    }


    public Node getBase() {
        return ref;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean isReference() {
        return false;
    }
}
