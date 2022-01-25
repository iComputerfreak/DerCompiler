package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.CondTarget;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.*;
import de.dercompiler.intermediate.operation.selection.SubstitutionRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.Cmp;
import firm.nodes.Cond;
import firm.nodes.Node;

import java.util.List;
import java.util.Objects;

public class CondRule extends SubstitutionRule<Cond> {
    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public List<Operation> substitute() {
        return createComparisonOp();
    }

    public List<Operation> createComparisonOp() {
        Node sel = getRootNode().getSelector();
        if (sel instanceof Cmp cmp) {
            Operation op = switch (cmp.getRelation()) {
                case False -> new Jmp(getTarget().getFalseTarget());
                case True -> new Jmp(getTarget().getTrueTarget());

                case Equal -> new Je(getTarget().getTrueTarget());
                case Less -> new Jl(getTarget().getTrueTarget());
                case Greater -> new Jg(getTarget().getTrueTarget());
                case LessEqual -> new Jle(getTarget().getTrueTarget());
                case GreaterEqual -> new Jge(getTarget().getTrueTarget());
                default -> null;
            };
            // If the jump is conditional, the "else"-case is necessary
            if (op instanceof Jmp) return List.of(op);
            return List.of(op, new Jmp(getTarget().getFalseTarget()));
        }
        return null;
    }

    private CondTarget getTarget() {
        if (getAnnotation(getRootNode()).getTarget() instanceof CondTarget ct) {
            return ct;
        }
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Conditional jump target not set correctly at node " + getRootNode().toString());
        throw new RuntimeException();
    }


    @Override
    public List<Node> getRequiredNodes(Graph realGraph) {
        return List.of();
    }

    @Override
    public boolean matches(Cond inputNode) {
        return !Objects.isNull(inputNode);
    }

    @Override
    public Operand createDefaultTarget() {
        return new CondTarget();
    }
}
