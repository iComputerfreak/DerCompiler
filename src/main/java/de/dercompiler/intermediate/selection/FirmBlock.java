package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Jmp;
import firm.nodes.Block;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FirmBlock {

    private final int nr;
    private boolean visited;
    private final List<FirmBlock> phis;
    private Map<Integer, Component> components;
    private CodeNode jumps;
    private int nextComponentID = 1;
    private boolean phiNode;

    public FirmBlock(int nr) {
        this.nr = nr;
        this.visited = false;
        phis = new LinkedList<>();
        components = new HashMap<>();
    }

    public FirmBlock(Block block) {
        this(block.getNr());
    }

    public int getNr() {
        return nr;
    }

    public List<Component> getComponents() {
        return List.copyOf(components.values());
    }

    public String getOperations() {
        finish();
        StringBuilder stringBuilder = new StringBuilder();

        // Phi code is in their own blocks now

        String blockCode = components.values().stream().filter(component -> !component.isEmpty()).map(cmp ->
                cmp.getOperations().stream()
                        .map(Operation::toString)
                        .collect(Collectors.joining("\n"))
        ).collect(Collectors.joining("\n------\n"));
        stringBuilder.append(blockCode);

        if (!Objects.isNull(jumps)) {
            stringBuilder.append("\n");
            String jumpCode = jumps.getOperations().stream().map(Object::toString).collect(Collectors.joining("\n"));
            stringBuilder.append(jumpCode);
        }
        return stringBuilder.toString();
    }

    private void finish() {
        if (hasPhis()) {
            System.out.println("Here!");
            for (FirmBlock phi : getPhis()) {
                List<Operation> linearization = new ArrayList<>();
                ArrayList<BinaryOperation> allMoves = new ArrayList<>(phi.components.values().stream()
                        .flatMap(c -> c.operations().stream())
                        .map(op -> (BinaryOperation) op).toList());

                // All registers that shall be overwritten but not read from are safe
                List<Operand> safeToWrite = new ArrayList<>(allMoves.stream().map(BinaryOperation::getTarget).toList());
                allMoves.stream().map(BinaryOperation::getSource).forEach(safeToWrite::remove);

                while (!allMoves.isEmpty()) {
                    if (safeToWrite.isEmpty()) {
                        VirtualRegister temp = new VirtualRegister();
                        Operand source = allMoves.get(0).getTarget();
                        Mov mov = new Mov(temp, source, false);
                        mov.setMode(allMoves.get(0).getMode());
                        linearization.add(mov);
                        allMoves.replaceAll(op -> {
                            if (op.getTarget() == source) {
                                Mov repl = new Mov(temp, op.getSource(), false);
                                repl.setMode(op.getMode());
                                return repl;
                            } else return op;
                        });
                        safeToWrite.add(temp);
                    }

                    Operand target = safeToWrite.remove(0);
                    for (int i = 0; i < allMoves.size(); i++) {
                        BinaryOperation mov = allMoves.get(i);
                        if (mov.getTarget().equals(target)) {
                            linearization.add(mov);
                            if (mov.getSource() instanceof VirtualRegister vreg && !safeToWrite.contains(vreg))
                                safeToWrite.add(vreg);
                            allMoves.remove(mov);
                            i--;
                        }
                    }

                }
                phi.components = Map.of(0, new Component(linearization));
            }

        }

        if (components.size() > 1) {
            // There may be a phi block part


        }
    }

    public void insertOperations(CodeNode ops) {
        if (ops.getOperations().isEmpty()) return;
        else if (this.isPhiNode()) return;
        components.putIfAbsent(ops.getComponent(), Component.getNew());
        components.get(ops.getComponent()).push(ops.getOperations());
    }

    public List<FirmBlock> getPhis() {
        return phis;
    }

    public void insertPhi(PhiNode phi) {

        for (int i = 0; i < phi.getPredCount(); i++) {
            FirmBlock phiBlock = this.phis.get(i);
            phiBlock.newComponent(phi.getCodeForPred(i));
        }
    }

    public void newComponent(CodeNode node) {
        int newId = nextComponentID++;
        components.put(newId, new Component(node.getOperations()));
    }

    public int newComponent() {
        return this.nextComponentID++;
    }

    public boolean getVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Override
    public String toString() {
        return "FirmBlock<" + nr + ">";
    }

    public void setJump(CodeNode node) {
        jumps = node;
    }

    public boolean hasPhis() {
        return !phis.isEmpty();
    }

    public void setPhiBlock(int idx, FirmBlock phiBlock) {
        phiBlock.setPhiNode(true);
        phiBlock.setJump(new CodeNode(List.of(new Jmp(new LabelOperand(this.getNr() + ""), true)), this));
        if (phis.size() == idx) phis.add(phiBlock);
        else if (phis.size() < idx)
            throw new RuntimeException("Cant add this phi block before all of its predecessors.");
        else phis.set(idx, phiBlock);
    }

    public boolean isPhiNode() {
        return phiNode;
    }

    private void setPhiNode(boolean phiNode) {
        this.phiNode = phiNode;
    }

    record Component(List<Operation> operations) {

        public static Component getNew() {
            return new Component(new LinkedList<>());
        }

        public List<Operation> getOperations() {
            return operations;
        }

        public void push(Operation op) {
            operations.add(0, op);
        }

        public void push(Collection<Operation> operations) {
            this.operations.addAll(0, operations);
        }

        public boolean isEmpty() {
            return operations.isEmpty();
        }
    }
}
