package de.dercompiler.transformation.node;

import de.dercompiler.semantic.type.Type;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

public class LocalVariableNode extends ReferenceNode {

    private int num;

    public LocalVariableNode(Type type, int num) {
        super(null, type);
        this.num = num;
    }

    @Override
    public Node genLoad(TransformationState state) {
        return state.construction.getVariable(num, mode);
    }

    @Override
    public ReferenceNode genStore(TransformationState state, ReferenceNode value) {
        state.construction.setVariable(num, value.genLoad(state));
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
        return false;
    }
}
