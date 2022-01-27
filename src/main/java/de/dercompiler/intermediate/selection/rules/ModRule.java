package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.NodeAnnotation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.nodes.Mod;
import firm.nodes.Node;

import java.util.List;

public class ModRule extends SubstitutionRule<Mod> {

    @Override
    public int getCost() {
        return 1 + getLeft().getCost() + getRight().getCost();
    }

    Mod getMod() {
        return getRootNode();
    }
    
    NodeAnnotation<?> getLeft() {
        return getAnnotation(getMod().getLeft());
    }

    NodeAnnotation<?> getRight() {
        return getAnnotation(getMod().getRight());
    }

    @Override
    public List<Operation> substitute() {
        Operation mod = new de.dercompiler.intermediate.operation.BinaryOperations.Mod(getLeft().getTarget(), getRight().getTarget());
        mod.setMode(getRootNode().getMode());
        setMode(getRootNode().getMode());
        return List.of(mod);
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Mod inputNode) {
        // any Mod node matches
        return true;
    }

}
