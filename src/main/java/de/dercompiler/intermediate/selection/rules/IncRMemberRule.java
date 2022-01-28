package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Inc;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.*;

import java.util.List;

public class IncRMemberRule extends SubstitutionRule<Store> {
    @Override
    public int getCost() {
        return 1;
    }

    private Operand getTarget() {
        return getAnnotation(getAdd().getLeft()).getTarget();
    }

    private Add getAdd() {
        if (node.getValue() instanceof Add add) return add;
        throw new RuntimeException();
    }

    public Const getConst() {
        if (getAdd().getRight() instanceof Const constant) return constant;
        throw new RuntimeException();
    }

    private Proj getProj() {
        if (getAdd().getLeft() instanceof Proj proj) return proj;
        throw new RuntimeException();
    }

    @Override
    public List<Operation> substitute() {
        Inc inc = new Inc(getTarget(), isMemoryOperation());
        inc.setMode(getAdd().getMode());
        setTarget(inc.getDefinition());
        return List.of(inc);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getAdd(), getConst(), getProj(),
                getLoad(), getLoad().getPtr());
    }

    private Load getLoad() {
        return getProj().getPred() instanceof Load load ? load : null;
    }


    @Override
    public boolean matches(Store inputNode) {
        return inputNode.getPtr() instanceof Member m1
                && inputNode.getValue() instanceof Add add
                && add.getRight() instanceof Const c1
                && c1.getTarval().asInt() == 1
                && add.getLeft() instanceof Proj proj
                && proj.getPred() instanceof Load load
                && load.getPtr() instanceof Member m2
                && m1.getEntity().equals(m2.getEntity())
                && m1.getPtr().equals(m2.getPtr());
    }
}
