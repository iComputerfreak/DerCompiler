package de.dercompiler.transformation.node;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.type.ArrayType;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.util.Utils;
import firm.Entity;
import firm.nodes.Node;

public class FieldNode extends ReferenceNode {

    public FieldNode(Node ref, Type type) {
        super(ref, type);
        this.type = type;
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        TransformationHelper.genStore(state, ref, value.genLoad(state), type.getFirmTransformationType());
        return value;
    }

    @Override
    public ReferenceNode prepareLoad(TransformationState state) {
        prepareNode(TransformationHelper.genLoad(state, ref, mode), NodeAccess.LOAD);
        return this;
    }

    @Override
    public ReferenceNode prepareAccessArray(TransformationState state) {
        ArrayType at = getTypeAsArray();
        return new ArrayNode(genLoad(state), at.getElementType(), at.getDimension()).prepareAccessArray(state);
    }

    @Override
    public ReferenceNode accessArray(TransformationState state, Node offset) {
        ArrayType at = getTypeAsArray();
        return new ArrayNode(genLoad(state), at.getElementType(), at.getDimension()).accessArray(state, offset);
    }

    @Override
    public ReferenceNode prepareGetObjectCallBase(TransformationState state) {
        return new ObjectNode(genLoad(state), getTypeAsClass()).prepareGetObjectCallBase(state);
    }

    @Override
    public ObjectNode getObjectCallBase(TransformationState state) {
        return new ObjectNode(genLoad(state), getTypeAsClass()); //checks for  class-type
    }

    @Override
    public boolean isReference() {
        return false;
    }
}
