package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.BinaryOperations.Cmp;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Jmp;
import de.dercompiler.intermediate.selection.rules.PhiRule;
import firm.nodes.Node;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public class CodeNode {

    private static int nextID = 0;
    private int component = -1;

    public CodeNode(List<Operation> operations, FirmBlock block) {
        this(operations, block, nextID());
    }

    public CodeNode(Operation op, FirmBlock block) {
        this(List.of(op), block);
    }

    public static int nextID() {
        return nextID++;
    }

    private final int id;
    private final FirmBlock firmBlock;
    private final List<Operation> operations;

    public CodeNode(List<Operation> operations, FirmBlock block, int id) {
        this.id = id; //CodeNode.nextID();
        this.operations = operations;
        this.firmBlock = block;
    }

    public int getId() {
        return id;
    }

    public FirmBlock getFirmBlock() {
        return firmBlock;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public boolean isPhi() {
        return false;
    }

    public boolean isJump() {
        return getOperations().stream().anyMatch(op -> op instanceof Jmp || op instanceof Ret);
    }

    public void setComponent(int component) {
        this.component = component;
    }

    public int getComponent() {
        return component;
    }

    public boolean isCondition() {
        List<Operation> operations = getOperations();
        return operations != null && !operations.isEmpty() && operations.get(0) instanceof Cmp;
    }

    @Override
    public String toString() {
        return "CodeNode<%d:%s/%d>".formatted(id, firmBlock.getId(), component);
    }
}
