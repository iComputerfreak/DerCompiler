package de.dercompiler.intermediate.ordering;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.JumpOperation;
import de.dercompiler.intermediate.selection.BasicBlockGraph;
import de.dercompiler.intermediate.selection.CodeNode;
import de.dercompiler.intermediate.selection.FirmBlock;
import de.dercompiler.util.GraphUtil;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MyBlockSorter implements BlockSorter {
    private List<LinkedList<FirmBlock>> chains;

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
            chains.add(new LinkedList<>(List.of(b)));
        });


        graph.getGraph().edgeSet().forEach(e -> graph.getGraph().setEdgeWeight(e, -1));

        GraphIterator<FirmBlock, DefaultWeightedEdge> blocks = new DepthFirstIterator<>(graph.getGraph());
        while (blocks.hasNext()) {
            classify(blocks.next());
        }

        List<FirmBlock> sorted = chainsToList(chains);
        sorted.forEach(b -> b.setIsJumpTarget(isJmpTarget(b)));
        return sorted;
    }

    private boolean isJmpTarget(FirmBlock block) {
        return graph.vertexSet().stream()
                .flatMap(FirmBlock::getJumpTargets)
                .map(LabelOperand::getTarget).anyMatch(b -> b.equals(block.getId()));
    }

    private List<FirmBlock> chainsToList(List<LinkedList<FirmBlock>> chains) {
        LinkedList<List<FirmBlock>> candidates = new LinkedList<>();
        getLoopHeads().forEach(cond -> candidates.add(getChain(cond)));
        candidates.add(getChain(getGraphData().getStart()));

        int noSuccess = 0;
        boolean hard = false;
        List<FirmBlock> chain = candidates.remove();
        outer:
        while (chains.size() > 1) {
            boolean success = false;

            FirmBlock last = chain.get(chain.size() - 1);
            List<FirmBlock> predecessors = GraphUtil.getPredecessors(last, graph);
            for (FirmBlock pred : predecessors) {
                List<FirmBlock> predChain = getChain(pred);
                candidates.remove(predChain);
                success = unify(last, pred, hard);
                if (success) {
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
            for (List<FirmBlock> firmBlocks : chains) {
                if (!candidates.contains(firmBlocks) && !firmBlocks.equals(chain)) {
                    candidates.add(0, firmBlocks);
                    break;
                }
            }


            //no success :c
            noSuccess++;
            if (noSuccess > chains.size()) hard = true;
            if (noSuccess > chains.size()*3) return failStrategy();
            if (!candidates.contains(chain))
                candidates.add(chain);
            chain = candidates.remove();

        }

        return getChain(getGraphData().getStart());
    }

    private LinkedList<FirmBlock> failStrategy() {

        LinkedList<FirmBlock> startChain = getChain(graphData.getStart());
        chains.remove(startChain);
        startChain.addAll(chains.stream().flatMap(List::stream).toList());
        return startChain;
        //new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.BLOCK_ORDER_FAIL, "The BlockSorter failed at the task of sorting the blocks. There must be a wacked chain");
    }

    private boolean unify(FirmBlock from, FirmBlock to, boolean hard) {
        List<FirmBlock> chainFrom = getChain(from);
        List<FirmBlock> chainTo = getChain(to);

        // !hard: try to chain only successive graphs, saving one jump instruction per link

        // hard: links chains even if that creates an unconditional jump
        if (chainFrom == null || chainTo == null || chainFrom.equals(chainTo)) return false;
            // this is the succession test
        else if (!hard && (!Objects.equals(chainFrom.get(chainFrom.size() - 1), from) || !Objects.equals(chainTo.get(0), to))) {
            return false;
        }

        // The first block should be the actual start block
        if (hard && chainTo.contains(getGraphData().getStart())) {
            FirmBlock temp = to;
            to = from;
            from = temp;

            List<FirmBlock> chainTemp = chainTo;
            chainTo = chainFrom;
            chainFrom = chainTemp;

        }

        if (!hard) {
            // implement fallthrough
            chainFrom.get(chainFrom.size() - 1).replaceJumpTarget(to.getId(), null);
        } else {
            to = chainTo.get(0);
        }

        from = chainFrom.get(chainFrom.size()-1);
        chainFrom.addAll(chainTo);
        chains.remove(chainTo);

        //System.out.printf("Unified %s-%s %s%n", from, to, hard ? " [x]" : "");
        return true;
    }

    private boolean unify(FirmBlock from, FirmBlock to) {
        return unify(from, to, false);
    }

    private LinkedList<FirmBlock> getChain(FirmBlock block) {
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
            if (bProps.getVisited()) return;
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

        if (bClass == BlockClass.IF_HEAD_AND_SINK) {
            possibleLoopHeads.push(block);
        }

        if (!bProps.isSeen()) {
            bProps.setSeen(true);
            for (FirmBlock pred : GraphUtil.getPredecessors(block, graph)) {
                if (getProps(pred) != null && getProps(pred).getVisited())
                    continue;
                classify(pred);

                BlockProperties props = getProps(pred);
                if (props.getBlockClass() == BlockClass.WHILE_HEAD) {
                    handleWhileCondition(block, pred);
                } else if (props.getBlockClass() == BlockClass.STRING) {
                    handleString(block, pred);
                } else if (props.getBlockClass().isBranchHead()) {
                    handleIfSimple(pred);
                }
                if (props.getBlockClass().isSink()) {
                    handleSinkSimple(pred);
                }
            }
        }
        // Return nodes and while heads need to be visited one more time to set the chains right
        if (bProps.getBlockClass() == BlockClass.SINK && succs == 0) {
            handleSinkSimple(block);
        } else if (bProps.getBlockClass() == BlockClass.WHILE_HEAD && bClass == BlockClass.WHILE_HEAD)
            return;

        bProps.setVisited(true);
    }

    private void handleIfSimple(FirmBlock block) {
        // Desired order: Cond (cj True) False (j EndIf) True EndIf
        if (checkWhileConditionExtension(block)) return;
        if (checkIfConditionExtension(block)) return;

        List<FirmBlock> predecessors = GraphUtil.getPredecessors(block, graph);

        JumpOperation trueJump = (JumpOperation) block.getJumps().get(0);
        LabelOperand target = (LabelOperand) trueJump.getArg();

        FirmBlock startOfTrueBranch = blocks.get(target.getTarget());
        FirmBlock startOfFalseBranch = GraphUtil.getOtherPred(block, startOfTrueBranch, graph);

        unify(block, startOfTrueBranch);
        List<Operation> elseOperations = getChain(startOfFalseBranch).getLast().getOperations();
        if (elseOperations.get(elseOperations.size() - 1) instanceof Ret) {
            // branches dont join anymore, no need to unify
        } else {
            unify(startOfTrueBranch, startOfFalseBranch, true);
        }
    }

    private void handleSinkSimple(FirmBlock block) {
        // successor with highest edge weight might do the trick
        List<DefaultWeightedEdge> defaultWeightedEdges = graph.outgoingEdgesOf(block).stream().toList();
        DefaultWeightedEdge nthEdge = defaultWeightedEdges.get(defaultWeightedEdges.size() - 1);

        FirmBlock nthSuccessor = graph.getEdgeTarget(nthEdge);
        // cannot have been visited yet
        if (getProps(nthSuccessor) == null || getProps(nthSuccessor).getVisited()) return;
        unify(nthSuccessor, block);
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
        // Maybe this loop condition is chained by AND or OR. In this case, detect more while-heads
        // If not found, they ruin the treatment of if-heads.
        List<FirmBlock> successors = GraphUtil.getSuccessors(block, graph);

        // if getProps(b) == null, that cannot be a while loop
        if (!successors.stream().allMatch(b -> getProps(b) != null && getProps(b).getBlockClass() == BlockClass.WHILE_HEAD))
            return false;
        FirmBlock loopCondition = successors.get(0);

        FirmBlock endOfCond = GraphUtil.getOtherPred(loopCondition, block, graph);
        if (GraphUtil.getPredecessors(block, graph).contains(endOfCond)) {
            //-> block is a condition extension, as in the 'b' of 'a && b' or 'a || b'
            getProps(block).setExtension(true);
            unify(loopCondition, block);
            getProps(block).setBlockClass(BlockClass.WHILE_HEAD);
            ifSinks.remove(block);
            ifSinks.remove(endOfCond);
            return true;
        } else if (getProps(endOfCond) != null && getProps(endOfCond).isExtension()) {
            unify(block, endOfCond);
        }
        return false;
    }

    private boolean checkIfConditionExtension(FirmBlock block) {
        // Maybe the if condition is chained by AND or OR. In this case,
        List<FirmBlock> successors = GraphUtil.getSuccessors(block, graph);

        //if successors are null, they are no branch heads
        if (!getProps(block).getBlockClass().isBranchHead() || !successors.stream().allMatch(p -> getProps(p) != null && getProps(p).bClass.isBranchHead())) {
            return false;
        }

        FirmBlock predInExtension = successors.get(0);

        FirmBlock commonSink = GraphUtil.getOtherPred(predInExtension, block, graph);
        if (commonSink != null && GraphUtil.getPredecessors(block, graph).contains(commonSink)) {
            //-> block is a condition extension
            //System.out.println("Es sieht ein bisschen so aus, als wäre %s eine if-Extension".formatted(block));
            unify(predInExtension, block);
            getProps(block).setExtension(true);
            ifSinks.remove(block);
            ifSinks.remove(commonSink);

            FirmBlock elseSink = GraphUtil.getOtherPred(block, commonSink, graph);
            if (!getProps(elseSink).getBlockClass().isBranchHead()) {
                unify(block, commonSink);
            }

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
        BlockClass blockClass = getProps(block).getBlockClass();
        if (blockClass.hasOneSuccessor()) {
            unify(block, pred, false);
        } else if (blockClass == BlockClass.WHILE_HEAD) {
            unify(block, pred, true);
        }

        // If without else: unify condition with then block to enforce topological order
        else if (blockClass.isBranchHead()) {
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
        if (!unify(block, cond) && !unify(block, cond, true)) {
            return;
        }

        ifSinks.remove(cond);

        FirmBlock top;
        if (!possibleLoopHeads.contains(cond)) return;
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
        private boolean extension;

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

        public boolean getVisited() {
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

        public boolean isExtension() {
            return extension;
        }

        public void setExtension(boolean extension) {
            this.extension = extension;
        }
    }
}
