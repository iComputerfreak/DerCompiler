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
        ReferenceNode res = new ArrayNode(genLoad(state), at, at.getDimension()).accessArray(state, offset);

        if (state.expectValue()) {
            TransformationHelper.booleanValueToConditionalJmp(state, res.genLoad(state));
            return null;
        } else {
            return res;
        }
    }

    @Override
    public boolean isReference() {
        return true;
    }
}
