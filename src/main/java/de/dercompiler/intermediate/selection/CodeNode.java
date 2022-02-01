package de.dercompiler.intermediate.selection;

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
    private int component;

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

    private static CodeSelector selector;

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

    private static FirmBlock getOrCreateFirmBlock(int id) {
        return selector.getOrCreateFirmBlock(id);
    }

    public void setComponent(int component) {
        this.component = component;
    }

    public int getComponent() {
        return component;
    }
}