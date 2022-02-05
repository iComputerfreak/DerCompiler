package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Jmp;
import de.dercompiler.intermediate.operation.UnaryOperations.JumpOperation;
import de.dercompiler.intermediate.operation.UnaryOperations.LabelOperation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.nodes.Block;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FirmBlock {

    private final String id;
    private boolean visited;
    private final List<FirmBlock> phis;
    private Map<Integer, Component> components;
    private CodeNode jumps;
    private int nextComponentID = 1;
    private boolean phiNode;
    private int predCount;
    private boolean isJumpTarget;
    private FirmBlock mainNode;
    private CodeNode comparison;

    public FirmBlock(String id) {
        this.id = id;
        this.visited = false;
        phis = new LinkedList<>();
        components = new HashMap<>();
    }

    public FirmBlock(Block block) {
        this(block.getNr() + "");
        this.predCount = block.getPredCount();
    }

    public FirmBlock(FirmBlock main, int phiIdx) {
        this(main.getIdForPhiNode(phiIdx));
        setMainNode(main);
    }

    public String getId() {
        return id;
    }

    public List<Component> getComponents() {
        return List.copyOf(components.values());
    }

    public List<Operation> getOperations() {
        List<Operation> operations = new LinkedList<>();

        if (isJumpTarget())
            operations.add(new LabelOperation(this.getId()));

        operations.addAll(components.values().stream().filter(component -> !component.isEmpty()).flatMap(cmp ->
                cmp.getOperations().stream()).toList());

        //new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("%s: %d cond, %d jumps".formatted(this, comparison == null ? 0 : comparison.getOperations().size(), jumps == null ? 0 : jumps.getOperations().size()));
        if (comparison != null && jumps != null && jumps.getOperations().size() >= 1) {
            operations.addAll(comparison.getOperations());
        }
        if (jumps != null) {
            operations.addAll(jumps.getOperations());
        }

        return operations;
    }

    public String getText() {
        finish();
        StringJoiner joiner = new StringJoiner("\n");

        // Phi code is in their own blocks now
        String blockCode = getOperations().stream()
                .map(Operation::toString)
                .collect(Collectors.joining("\n"));
        joiner.add(blockCode);

        return joiner.toString();
    }

    private void finish() {
        if (hasPhis()) {
            for (FirmBlock phi : getPhis()) {
                List<Operation> linearization = eliminatePhi(phi);
                phi.components = Map.of(0, new Component(linearization));
            }
        }

        // TODO There may be a phi block part, a component that has no memory operations whatsoever and that ultimately
        //  only deals with one phi variable. It might be useful to move it to the top of the actual phi block

    }

    private List<Operation> eliminatePhi(FirmBlock phi) {
        List<Operation> linearization = new ArrayList<>();
        ArrayList<BinaryOperation> allMoves = phi.components.values().stream()
                .flatMap(c -> c.operations().stream())
                .map(op -> (BinaryOperation) op)
                .filter(op -> !op.getSource().equals(op.getTarget()))
                .collect(Collectors.toCollection(ArrayList::new));

        // All registers that shall be overwritten but not read from are safe
        List<Operand> safeToWrite = new ArrayList<>(allMoves.stream().map(BinaryOperation::getTarget).toList());
        allMoves.stream().map(BinaryOperation::getSource).forEach(safeToWrite::remove);

        while (!allMoves.isEmpty()) {
            if (safeToWrite.isEmpty()) {
                // Introduce temp register to save one of the registers to
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
        return linearization;
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
        return "FirmBlock<" + id + ">";
    }

    public void setJump(CodeNode node) {
        jumps = node;
    }

    public boolean hasPhis() {
        return !phis.isEmpty();
    }

    public void setPhiBlock(int idx, FirmBlock phiBlock) {
        phiBlock.markPhiNode();
        phiBlock.setJump(new CodeNode(List.of(new Jmp(new LabelOperand(this.getId()))), this));
        if (phis.size() == idx) phis.add(phiBlock);
        else if (phis.size() < idx)
            throw new RuntimeException("Cant add this phi block before all of its predecessors.");
        else phis.set(idx, phiBlock);
    }

    public boolean isPhiNode() {
        return phiNode;
    }

    private void markPhiNode() {
        this.phiNode = true;
    }

    public List<Operation> getJumps() {
        if (jumps == null) return List.of();
        return List.copyOf(jumps.getOperations());
    }

    public void replaceJumpTarget(String oldTarget, String newTarget) {
        List<Operation> jmps = jumps.getOperations();

        List<String> targets = jmps.stream()
                .filter(jmp -> jmp instanceof JumpOperation)
                .map(jmp -> (LabelOperand) jmp.getArgs()[0])
                .map(LabelOperand::getTarget).toList();

        if (targets.isEmpty()) return;

        int replaceIdx = IntStream.of(0, targets.size() - 1)
                .filter(i -> targets.get(i).equals(oldTarget))
                .findAny().orElse(-1);

        switch (jmps.size()) {
            case 1 -> {
                if (replaceIdx == 0) {
                    setJump(newTarget != null ? new CodeNode(new Jmp(new LabelOperand(newTarget)), this) : null);
                }
            }
            case 2 -> {
                if (replaceIdx == 0) {
                    JumpOperation condJmp = (JumpOperation) jmps.get(0);
                    if (newTarget != null) {
                        JumpOperation replace = condJmp.setTo(new LabelOperand(newTarget));
                        setJump(new CodeNode(replace, this));
                    } else {
                        JumpOperation invert = condJmp.invert((LabelOperand) jmps.get(1).getArgs()[0]);
                        setJump(new CodeNode(invert, this));
                    }
                } else if (replaceIdx == 1) {
                    Jmp jmp1 = (Jmp) jmps.get(1);
                    if (newTarget != null) {
                        Jmp replace = jmp1.setTo(new LabelOperand(newTarget));
                        setJump(new CodeNode(replace, this));
                    } else {
                        setJump(new CodeNode(jmps.get(0), this));
                    }
                }
            }

            // it is allowed that the oldTarget does not match any of the current targets in case of hard unification
        }
    }

    public int getPredCount() {
        return predCount;
    }

    public String getIdForPhiNode(int idx) {
        return this.id + "_" + idx;
    }

    public boolean hasReturn() {
        return Objects.nonNull(jumps) && jumps.getOperations().get(0) instanceof Ret;
    }

    public Stream<LabelOperand> getJumpTargets() {
        return jumps == null ? Stream.<LabelOperand>builder().build() :
                jumps.getOperations().stream()
                .filter(op -> op instanceof JumpOperation).map(op -> (LabelOperand) op.getArgs()[0]);
    }

    public void setIsJumpTarget(boolean isJumpTarget) {
        this.isJumpTarget = isJumpTarget;
    }

    public boolean isJumpTarget() {
        return isJumpTarget;
    }

    public FirmBlock getMainNode() {
        return mainNode;
    }

    public void setMainNode(FirmBlock mainNode) {
        this.mainNode = mainNode;
    }

    public CodeNode getComparison() {
        return comparison;
    }

    public void setComparison(CodeNode comparison) {
        this.comparison = comparison;
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
