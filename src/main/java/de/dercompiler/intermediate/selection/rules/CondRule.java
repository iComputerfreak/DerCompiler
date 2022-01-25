package de.dercompiler.intermediate.selection.rules;

import de.dercompiler.intermediate.operand.CondTarget;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.*;
import de.dercompiler.intermediate.selection.SubstitutionRule;
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

            Operation op;

            op = switch (cmp.getRelation()) {
                case True -> new Jmp(getTarget().getTrueTarget(), isMemoryOperation());
                case False -> new Jmp(getTarget().getFalseTarget(), isMemoryOperation());
                case Equal -> new Je(getTarget().getTrueTarget(), isMemoryOperation());
                case LessGreater -> new Jne(getTarget().getTrueTarget(), isMemoryOperation());
                default -> null;
            };

            if (op == null) {
                if (cmp.getMode().isSigned()) {
                    op = switch (cmp.getRelation()) {
                        case Less -> new Jl(getTarget().getTrueTarget(), isMemoryOperation());
                        case Greater -> new Jg(getTarget().getTrueTarget(), isMemoryOperation());
                        case LessEqual -> new Jle(getTarget().getTrueTarget(), isMemoryOperation());
                        case GreaterEqual -> new Jge(getTarget().getTrueTarget(), isMemoryOperation());
                        default -> null;
                    };
                } else {
                    // unsigned
                    op = switch (cmp.getRelation()) {
                        case Less -> new Jb(getTarget().getTrueTarget(), isMemoryOperation());
                        case Greater -> new Ja(getTarget().getTrueTarget(), isMemoryOperation());
                        case LessEqual -> new Jbe(getTarget().getTrueTarget(), isMemoryOperation());
                        case GreaterEqual -> new Jae(getTarget().getTrueTarget(), isMemoryOperation());
                        default -> null;
                    };
                }
            }
            if (op == null) {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Unexpected Cmp relation: " + cmp.getRelation().toString());
            }
            // If the jump is conditional, the "else"-case is necessary
            op.setMode(cmp.getMode());
            if (op instanceof Jmp) return List.of(op);
            return List.of(op, new Jmp(getTarget().getFalseTarget(), isMemoryOperation()));
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
