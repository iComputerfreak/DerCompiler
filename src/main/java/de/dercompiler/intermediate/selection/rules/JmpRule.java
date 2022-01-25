package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Jmp;
import firm.nodes.Node;

import java.util.List;
import java.util.Objects;

public class JmpRule extends SubstitutionRule<Jmp> {
    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public List<Operation> substitute() {
        Operand target = getAnnotation(getRootNode()).getTarget();
        if (!Objects.isNull(target) && target instanceof LabelOperand lbl) {
            de.dercompiler.intermediate.operation.UnaryOperations.Jmp jmp = new de.dercompiler.intermediate.operation.UnaryOperations.Jmp(lbl);
            jmp.setMode(getRootNode().getMode());
            return List.of(jmp);
        }
        // we never return
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Jump target was not set correctly: " + target);
        throw new RuntimeException();
    }

    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Jmp inputNode) {
        return !Objects.isNull(inputNode);
    }

    @Override
    public boolean needsJmpTarget() {
        return true;
    }
}
