package de.dercompiler.transformation.node;

import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

public class ArrayNode extends ReferenceNode {

    private int dim;

    public ArrayNode(Node ref, Type type, int dimension) {
        super(ref, type);
        this.dim = dimension;
    }

    @Override
    public Node genLoad(TransformationState state) {
        return genRefLoad(state).genLoad(state);
    }

    public ReferenceNode genRefLoad(TransformationState state) {
        Node res = TransformationHelper.genLoad(state, ref, isElement() ? mode : Mode.getP());
        if (isElement()) {
            return new RValueNode(res, type);
        }
        return new ArrayNode(res, type, dim - 1);
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        TransformationHelper.genStore(state, ref, value.genLoad(state), isElement() ? type() : Mode.getP().getType());
        return value;
    }

    @Override
    public ReferenceNode accessArray(TransformationState state, Node offset) {
        return null;
    }

    private firm.Type type() {
        return this.type.getFirmType();
    }

    @Override
    public ReferenceNode accessField(TransformationState state) {
        return null;
    }

    @Override
    public ReferenceNode callMethod(TransformationState state) {
        return null;
    }

    public Node getPointer() {
        return ref;
    }

    public Type getElementType() {
        return type;
    }

    public int getDimension() {
        return dim;
    }

    public boolean isElement() {
        return dim == 0;
    }

    @Override
    public boolean isReference() {
        return true;
    }
}
