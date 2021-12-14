package de.dercompiler.transformation.node;

import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

public class LocalVariableNode extends ReferenceNode {

    private int num;

    public LocalVariableNode(Mode mode, int num) {
        super(null, mode);
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
    public boolean isReference() {
        return false;
    }
}
