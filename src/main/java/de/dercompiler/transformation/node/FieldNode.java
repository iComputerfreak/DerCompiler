package de.dercompiler.transformation.node;

import de.dercompiler.ast.type.CustomType;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.CompoundType;
import firm.Mode;
import firm.nodes.Node;

public class FieldNode extends ReferenceNode {


    public FieldNode(Node ref, Type type) {
        super(ref, type);
        this.type = type;
    }

    @Override
    public Node genLoad(TransformationState state) {
        return TransformationHelper.genLoad(state, ref, mode);
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        TransformationHelper.genStore(state, ref, value.genLoad(state), type.getFirmType());
        return value;
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
        return true;
    }
}
