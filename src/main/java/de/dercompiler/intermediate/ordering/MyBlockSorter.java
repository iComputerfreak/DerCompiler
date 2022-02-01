package de.dercompiler.intermediate.ordering;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.JumpOperation;
import de.dercompiler.intermediate.selection.BasicBlockGraph;
import de.dercompiler.intermediate.selection.CodeNode;
import de.dercompiler.intermediate.selection.FirmBlock;
import de.dercompiler.util.GraphUtil;
import firm.Firm;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MyBlockSorter implements BlockSorter {
    private List<ArrayList<FirmBlock>> chains;

    private Graph<FirmBlock, DefaultWeightedEdge> graph;
    private Map<String, FirmBlock> blocks;
    private GraphData graphData;
    private Stack<FirmBlock> possibleLoopHeads;
    private Stack<FirmBlock> ifSinks;


    @Override
    public List<FirmBlock> sortBlocks(BasicBlockGraph graph) {
        this.chains = new ArrayList<>();
        this.graph = graph.getGraph();
        this.graphData = new GraphData();
        this.blocks = new HashMap<>();
        this.possibleLoopHeads = new Stack<>();
        this.ifSinks = new Stack<>();
        BlockProperties.props.clear();

        graph.getGraph().vertexSet().forEach(b -> {
            blocks.put(b.getId(), b);
            chains.add(new ArrayList<>(List.of(b)));
        });


        graph.getGraph().edgeSet().forEach(e -> graph.getGraph().setEdgeWeight(e, -1));

        GraphIterator<FirmBlock, DefaultWeightedEdge> blocks = new DepthFirstIterator<>(graph.getGraph());
        while (blocks.hasNext()) {
            classify(blocks.next());
        }

        List<FirmBlock> sorted = chainsToList(chains);
        return sorted;
    }

    private boolean isJmpTarget(FirmBlock block) {
        return graph.vertexSet().stream()
                .flatMap(FirmBlock::getJumpTargets)
                .map(LabelOperand::getTarget).anyMatch(b -> b.equals(block.getId()));
    }

    private List<FirmBlock> chainsToList(List<ArrayList<FirmBlock>> chains) {
        LinkedList<ArrayList<FirmBlock>> candidates = new LinkedList<>();
        getLoopHeads().forEach(cond -> candidates.add(getChain(cond)));
        candidates.add(getChain(getGraphData().getStart()));

        int noSuccess = 0;
        boolean hard = false;
        ArrayList<FirmBlock> chain = candidates.remove();
        outer:
        while (chains.size() > 1) {
            boolean success = false;

            FirmBlock last = chain.get(chain.size() - 1);
            List<FirmBlock> predecessors = GraphUtil.getPredecessors(last, graph);
            for (FirmBlock pred : predecessors) {
                ArrayList<FirmBlock> predChain = getChain(pred);
                candidates.remove(predChain);
                success = unify(last, pred, hard);
                if (success) {
                    //System.out.printf("Unified %s-%s%n", last, pred);
                    noSuccess = 0;
                    candidates.remove(predChain);
                    continue outer;
                }
            }
            FirmBlock first = chain.get(0);
            List<FirmBlock> successors = GraphUtil.getSuccessors(first, graph);
            for (FirmBlock succ : successors) {
                success = unify(succ, first, hard);
                if (success) {
                    //System.out.printf("Unified %s-%s%n", succ, first);
                    candidates.remove(chain);
                    candidates.add(chain = getChain(succ));
                    noSuccess = 0;
                    continue outer;
                }
            }
            for (ArrayList<FirmBlock> firmBlocks : chains) {
                if (!candidates.contains(firmBlocks) && !firmBlocks.equals(chain)) {
                    candidates.add(0, firmBlocks);
                    break;
                }
            }


            //no success :c
            noSuccess++;
            if (noSuccess > chains.size()) hard = true;

            if (!candidates.contains(chain))
                candidates.add(chain);
            chain = candidates.remove();
            //System.out.println("Try " + chain.get(0));

        }

        return getChain(getGraphData().getStart());
    }

    private boolean unify(FirmBlock from, FirmBlock to, boolean hard) {
        ArrayList<FirmBlock> chainFrom = getChain(from);
        ArrayList<FirmBlock> chainTo = getChain(to);

        // hard: links chains even if that creates an unconditional jump
        if (chainFrom == null || chainTo == null
                || chainFrom.equals(chainTo)
                || !hard && (!Objects.equals(chainFrom.get(chainFrom.size() - 1), from)
                || !Objects.equals(chainTo.get(0), to))) {
            return false;
        }

        // The first block should be the actual start block
        if (hard && chainTo.contains(getGraphData().getStart())) {
            FirmBlock temp = to;
            to = from;
            from = temp;

            ArrayList<FirmBlock> chainTemp = chainTo;
            chainTo = chainFrom;
            chainFrom = chainTemp;

        }

        if (!hard) {
            // implement fallthrough
            chainFrom.get(chainFrom.size() - 1).replaceJumpTarget(to.getId(), null);
        } else {
            to = chainTo.get(0);
        }

        chainFrom.addAll(chainTo);
        chains.remove(chainTo);

        to.setIsJumpTarget(isJmpTarget(to));

        return true;
    }

    private boolean unify(FirmBlock from, FirmBlock to) {
        return unify(from, to, false);
    }

    private ArrayList<FirmBlock> getChain(FirmBlock block) {
        return chains.stream().filter(c -> c.contains(block)).findFirst().orElse(null);
    }

    private List<FirmBlock> getLoopHeads() {
        // sorted by depth of nesting
        return BlockProperties.filter(prop -> prop.depth > 0)
                .sorted(Comparator.comparingInt(prop -> -prop.depth))
                .map(BlockProperties::getBlock).toList();
    }

    private void classify(FirmBlock block) {

        BlockProperties bProps = getProps(block);
        if (bProps != null) {
            if (bProps.isVisited()) return;
        } else {
            bProps = new BlockProperties(block);
        }
        checkJmpTargets(block);
        boolean whileHead = bProps.isSeen();

        int succs = graph.inDegreeOf(block);
        int preds = graph.outDegreeOf(block);

        BlockClass bClass = BlockClass.UNKNOWN;
        if (preds == 0) getGraphData().setStart(block);
        if (succs == 1 && preds <= 1) bClass = BlockClass.STRING;
        else if (succs > 1 && preds > 1)
            bClass = whileHead ? BlockClass.WHILE_HEAD : BlockClass.IF_HEAD_AND_SINK;
        else if (succs > 1) bClass = BlockClass.IF_HEAD;
        else if (preds > 1) bClass = BlockClass.SINK;
        bProps.setBlockClass(bClass);

        //System.out.printf("FirmBlock %s seems to be a %s%n", block.getId(), bClass);

        if (bClass == BlockClass.IF_HEAD_AND_SINK) {
            possibleLoopHeads.push(block);
        }

        if (!bProps.isSeen()) {
            bProps.setSeen(true);
            for (FirmBlock pred : GraphUtil.getPredecessors(block, graph)) {
                if (getProps(pred) != null && getProps(pred).isVisited())
                    continue;
                classify(pred);

                BlockProperties props = getProps(pred);
                if (props.getBlockClass() == BlockClass.WHILE_HEAD) {
                    handleWhileCondition(block, pred);
                } else if (props.getBlockClass() == BlockClass.STRING) {
                    handleString(block, pred);
                }
                else if (props.getBlockClass().isBranchHead()) {
                    handleIfSimple(pred);
                }
                if (props.getBlockClass().isSink()) {
                    handleSinkSimple(pred);
                }
            }
        }

        bProps.setVisited(true);
    }

    private void handleIfSimple(FirmBlock block) {
        // Desired order: Cond (cj True) False (j EndIf) True EndIf
        if (checkWhileConditionExtension(block)) return;

        FirmBlock startOfTrueBranch = GraphUtil.getPredecessors(block, graph).get(0);

        unify(block, startOfTrueBranch);
    }

    private void handleSinkSimple(FirmBlock block) {
        FirmBlock endOfFalseBranch = GraphUtil.getSuccessors(block, graph).get(0);
        // cannot have been visited yet
        if (endOfFalseBranch.getVisited()) return;
        unify(endOfFalseBranch, block);
    }


    private void handleIf(FirmBlock block) {
        // Desired order: Cond (cj True) False (j EndIf) True EndIf

        /*
            Sinks need to be pushed n-1 times for n successors!
         */

        // This happens if this block is an if head and sink as well: gets visited twice
        if (block == ifSinks.peek()) return;

        if (checkWhileConditionExtension(block)) return;
        if (checkIfConditionExtension(block)) {
            ifSinks.pop();
            return;
        }

        FirmBlock sink = ifSinks.pop();
        int offset = (int) ifSinks.stream().filter(b -> b.equals(sink)).count();
        FirmBlock startOfTrueBranch = GraphUtil.getPredecessors(block, graph).get(0);
        FirmBlock startOfFalseBranch = GraphUtil.getPredecessors(block, graph).get(1);
        FirmBlock endOfFalseBranch = GraphUtil.getSuccessors(sink, graph).get(0 + offset);
        FirmBlock endOfTrueBranch = GraphUtil.getSuccessors(sink, graph).get(1 + offset);

        if (sink.equals(startOfTrueBranch) || block.equals(endOfFalseBranch)) {
            // if w/o else, handled in handleString(block)
            return;
        }

        unify(block, startOfTrueBranch);
        unify(endOfTrueBranch, startOfFalseBranch);
        unify(endOfFalseBranch, sink);

        //System.out.printf("unify %s -> %s ../.. %s -> %s%n", block, startOfTrueBranch, endOfFalseBranch, sink);

    }

    private boolean checkWhileConditionExtension(FirmBlock block) {
        // Maybe the loop condition is chained by AND or OR. In this case, detect more while-heads
        // If not found, they ruin the treatment of if-heads.
        List<FirmBlock> successors = GraphUtil.getSuccessors(block, graph);
        if (successors.size() != 1 || getProps(successors.get(0)).getBlockClass() != BlockClass.WHILE_HEAD)
            return false;
        FirmBlock loopCondition = successors.get(0);

        FirmBlock beginOfLoop = GraphUtil.getOtherPred(loopCondition, block, graph);
        if (GraphUtil.getPredecessors(block, graph).contains(beginOfLoop)) {
            //-> block is a condition extension
            unify(loopCondition, block);
            getProps(block).setBlockClass(BlockClass.WHILE_HEAD);
            ifSinks.remove(block);
            ifSinks.remove(beginOfLoop);
            return true;
        }
        return false;
    }
    private boolean checkIfConditionExtension(FirmBlock block) {
        // Maybe the if condition is chained by AND or OR. In this case, their sink is all the same.
        List<FirmBlock> successors = GraphUtil.getSuccessors(block, graph);
        BlockProperties succProps = getProps(successors.get(0));
        if (succProps == null || succProps.getBlockClass() != BlockClass.IF_HEAD)
            return false;
        FirmBlock extdCond = successors.get(0);

        FirmBlock beginOfCond = GraphUtil.getOtherPred(extdCond, block, graph);
        if (GraphUtil.getPredecessors(block, graph).contains(beginOfCond)) {
            //-> block is a condition extension
            unify(extdCond, block);
            getProps(block).setBlockClass(BlockClass.WHILE_HEAD);
            ifSinks.remove(block);
            ifSinks.remove(beginOfCond);
            return true;
        }
        return false;
    }

    private void checkJmpTargets(FirmBlock pred) {
        List<Operation> jumps = pred.getJumps();
        List<LabelOperand> targets = jumps.stream().filter(op -> op instanceof JumpOperation)
                .map(op -> (JumpOperation) op)
                .map(JumpOperation::getTarget)
                .toList();
        boolean success = false;
        for (int i = 0; i < targets.size(); i++) {
            LabelOperand t = targets.get(i);

            if (!blocks.containsKey(t.getTarget()) && t.isPhiNode()) {
                // empty phi block that has not been realized
                jumps = new ArrayList<>(jumps);
                JumpOperation jmp = (JumpOperation) jumps.get(i);

                jumps.set(i, jmp.setTo(t.getMainNode()));
                success = true;
            }
        }
        if (success) pred.setJump(new CodeNode(jumps, pred));
    }



    private void handleString(FirmBlock block, FirmBlock pred) {
        //Unify chains of Strings
        if (getProps(block).getBlockClass().hasOneSuccessor()) unify(block, pred);

            // If without else: unify condition with then block to enforce topological order
        else if (getProps(block).getBlockClass().isBranchHead()) {
            FirmBlock sinkBlock = GraphUtil.getOtherPred(block, pred, graph);
            FirmBlock thenBlock = sinkBlock == null ? null : GraphUtil.getOtherSucc(sinkBlock, block, graph);

            // If this is an "if w/o else" situation, sinkBlock and thenBlock cannot be null
            if (thenBlock != null && thenBlock.equals(pred)) {
                unify(block, thenBlock);
                unify(thenBlock, sinkBlock);
            }
        }
    }

    private void handleWhileCondition(FirmBlock block, FirmBlock cond) {
        // This returns true iff block->cond is the edge out of the loop back to the condition.
        // This edge should be unified whenever possible.
        if (!unify(block, cond)) {
            return;
        }

        ifSinks.remove(cond);

        FirmBlock top;
        while ((top = possibleLoopHeads.peek()) != cond) {
            BlockProperties topProps = getProps(top);
            if (topProps.getBlockClass() == BlockClass.WHILE_HEAD) {
                getProps(cond).addInnerLoop(top);
            }
            possibleLoopHeads.pop();
        }
    }

    private BlockProperties getProps(FirmBlock pred) {
        return BlockProperties.get(pred);
    }

    private GraphData getGraphData() {
        return this.graphData;
    }

    enum BlockClass {
        UNKNOWN, IF_HEAD, LOOP_HEAD, STRING, START, END, WHILE_HEAD, SINK, RETURN, IF_HEAD_AND_SINK, DEAD;

        public boolean isSink() {
            return this == SINK || this == IF_HEAD_AND_SINK;
        }

        public boolean hasOneSuccessor() {
            return this == STRING || this == SINK;
        }

        public boolean isBranchHead() {
            return this == IF_HEAD || this == IF_HEAD_AND_SINK;
        }
    }

    private static class GraphData {
        private FirmBlock start;
        private FirmBlock end;

        public void setStart(FirmBlock start) {
            this.start = start;
        }

        public FirmBlock getStart() {
            return start;
        }
    }

    private class BlockProperties {
        private static final Map<FirmBlock, BlockProperties> props = new HashMap<>();
        private final String id;
        private BlockClass bClass;
        private final List<FirmBlock> innerLoops;
        private boolean visited;
        private boolean seen;
        private int depth = 0;

        public BlockProperties(FirmBlock block) {
            props.put(block, this);
            id = block.getId();
            innerLoops = new ArrayList<>();
        }

        public static BlockProperties get(FirmBlock block) {
            return props.get(block);
        }

        public static Stream<BlockProperties> filter(Predicate<BlockProperties> predicate) {
            return props.values().stream().filter(predicate);
        }

        public void setBlockClass(BlockClass bClass) {
            this.bClass = bClass;
        }

        public BlockClass getBlockClass() {
            return bClass;
        }

        public void addInnerLoop(FirmBlock loop) {
            innerLoops.add(loop);
            if (depth == 0) depth = 1;
            get(loop).setDepth(depth + 1);
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        public boolean isSeen() {
            return seen;
        }

        public void setSeen(boolean seen) {
            this.seen = seen;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
            for (FirmBlock innerLoop : innerLoops) {
                get(innerLoop).setDepth(depth + 1);
            }
            //System.out.printf("Depth of %s is now %d.%n", id, depth);
        }

        public FirmBlock getBlock() {
            return blocks.get(id);
        }
    }
}
