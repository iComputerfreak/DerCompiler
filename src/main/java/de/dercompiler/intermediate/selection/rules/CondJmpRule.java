package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.Mode;
import firm.nodes.Node;
import firm.nodes.Proj;

import java.util.List;
import java.util.Objects;

public class CondJmpRule extends SubstitutionRule<Proj> {
    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public List<Operation> substitute() {
        Operand target = getAnnotation(getRootNode()).getTarget();
        if (!Objects.isNull(target) && target instanceof LabelOperand lbl) {
            return List.of(new de.dercompiler.intermediate.operation.UnaryOperations.Jmp(lbl));
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
    public boolean matches(Proj inputNode) {
        return !Objects.isNull(inputNode) && Objects.equals(inputNode.getMode(), Mode.getX());
    }

    @Override
    public boolean needsJmpTarget() {
        return true;
    }
}
