package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import firm.Graph;
import firm.Mode;
import firm.nodes.Member;
import firm.nodes.Node;

import java.util.List;
import java.util.Objects;

public class MemberRule extends SubstitutionRule<Member> {

    @Override
    public int getCost() {
        return 1 + getAnnotation(getObject()).getCost();
    }

    private Node getObject() {
        return getMember().getPtr();
    }

    @Override
    public List<Operation> substitute() {
        int offset = getMember().getEntity().getOffset();
        Operand object = getAnnotation(getObject()).getDefinition();
        Address target = Address.loadWithOffset(object, offset);
        setDefinition(target);
        return List.of();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    private Member getMember() {
        return getRootNode();
    }

    @Override
    public boolean matches(Member member) {
        return member != null
                && Objects.equals(member.getPtr().getMode(), Mode.getP());
    }
}
