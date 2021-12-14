package de.dercompiler.transformation.node;

import de.dercompiler.ast.type.CustomType;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.CompoundType;
import firm.Mode;
import firm.Type;
import firm.nodes.Node;

public class FieldNode extends ReferenceNode {

    private Type type;

    public FieldNode(Node ref, Type type) {
        super(ref, type.getMode());
        this.type = type;
    }

    @Override
    public Node genLoad(TransformationState state) {
        return TransformationHelper.genLoad(state, ref, mode);
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        TransformationHelper.genStore(state, ref, value.genLoad(state), type);
        return value;
    }

    @Override
    public boolean isReference() {
        return true;
    }
}
