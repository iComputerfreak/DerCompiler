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
        return getAnnotation(getSurvivingMember()).getDefinition();
    }

    private Node getSurvivingMember() {
        return getRootNode().getPtr();
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
    private Proj getSurvivingProj() {
        if (getRootNode().getPred(0) instanceof Proj proj) return proj;
        throw new RuntimeException();
    }

    private Load getLoad() {
        if (getProj().getPred() instanceof Load load) return load;
        throw new RuntimeException();
    }

    private Proj getProjM() {
        if (getRootNode().getMem() instanceof Proj proj) return proj;
        throw new RuntimeException();
    }

    @Override
    public List<Operation> substitute() {
        Inc inc = new Inc(getTarget(), isMemoryOperation());
        inc.setMode(getAdd().getMode());
        setDefinition(getAnnotation(getSurvivingProj()).getDefinition());
        inc.setDefinition(getDefinition());
        return List.of(inc);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of(getAdd(), getConst(), getProj(),
                getProjM(), getLoad(), getLoad().getPtr());
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
