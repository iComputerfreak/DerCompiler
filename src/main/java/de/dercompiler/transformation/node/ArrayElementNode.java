package de.dercompiler.transformation.node;

import de.dercompiler.semantic.type.ArrayType;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Node;

public class ArrayElementNode extends ReferenceNode {


    public ArrayElementNode(Node ref, Type type) {
        super(ref, type);
    }

    @Override
    public Node genLoad(TransformationState state) {
        return TransformationHelper.genLoad(state, ref, type.getFirmTransformationType().getMode());
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        TransformationHelper.genStore(state, ref, value.genLoad(state), value.getType().getFirmTransformationType());
        return value;
    }

    @Override
    public ObjectNode getObjectCallBase(TransformationState state) {
        ClassType ct = getTypeAsClass();
        return new ObjectNode(ref, ct);
    }

    @Override
    public ReferenceNode accessField(TransformationState state, String fieldName) {
        ClassType ct = getTypeAsClass();
        return new ObjectNode(ref, ct).accessField(state, fieldName);
    }

    @Override
    public ReferenceNode accessArray(TransformationState state, Node offset) {
        ArrayType at = getTypeAsArray();
        return new ArrayNode(genLoad(state), at, at.getDimension()).accessArray(state, offset);
    }

    @Override
    public boolean isReference() {
        if (!(type instanceof ArrayType at)) return false;
        if (at.getDimension() > 0) return true;
        return at.getElementType() instanceof ClassType;
    }
}
