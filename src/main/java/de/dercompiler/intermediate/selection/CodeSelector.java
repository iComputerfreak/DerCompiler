package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.CodeGenerationWarningIds;
import de.dercompiler.intermediate.operand.CondTarget;
import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.rules.CondJmpRule;
import de.dercompiler.intermediate.selection.rules.EmptyRule;
import de.dercompiler.intermediate.selection.rules.PhiRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.GraphDumper;
import de.dercompiler.util.GraphUtil;
import firm.BlockWalker;
import firm.nodes.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;

public class CodeSelector extends LazyNodeWalker implements BlockWalker {

    private static final OutputMessageHandler logger = new OutputMessageHandler(MessageOrigin.CODE_GENERATION);

    // The current mode of the CodeSelector
    private enum Mode {
        BLOCKS,
        ANNOTATION,
        CONSTRUCTION,
        TRANSFORMATION,
        LINEARIZATION
    }

    private final firm.Graph graph;
    // Contains the NodeAnnotation of a given node nr. from the real graph
    private final Map<Integer, NodeAnnotation<?>> annotations = new HashMap<>();
    private Mode mode = Mode.BLOCKS;
    private final Graph<NodeAnnotation<?>, DefaultEdge> nodeAnnotationGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private final Graph<CodeNode, DefaultEdge> codeGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private final Graph<FirmBlock, DefaultWeightedEdge> blocksGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
    private final Map<Integer, CodeNode> codeGraphLookup = new HashMap<>();
    private final HashMap<String, FirmBlock> firmBlocks = new HashMap<>();
    private final Map<Node, String> jmpTargets = new HashMap<>();
    private int nextIntermediateID = -1;
    private Node memSuccessor;

    /**
     * Creates a new CodeSelector with the given Graph and SubstitutionRules
     *
     * @param graph The graph to create intermediate code for
     */
    public CodeSelector(firm.Graph graph) {
        this.graph = graph;
        SubstitutionRule.annotationSupplier = node -> annotations.get(node.getNr());
    }

    /**
     * Returns the next free ID to be used for the intermediate blocks in the FirmBlock graph
     */
    private String nextIntermediateID() {
        return Integer.toString(nextIntermediateID--);
    }

    /**
     * Generates intermediate code for the Graph that was given when creating this class
     * using the set of rules that was given.
     * <p>
     * The algorithm will run in 3 phases.
     * <p>
     * In the first phase, the algorithm will annotate the firm graph, walking it from top to bottom
     * (i.e. from {@link firm.nodes.Start} to {@link firm.nodes.End}), annotating each node with the cheapest rule that
     * can be used to substitute this node (potentially including some or all predecessors), taking into consideration
     * any left-out predecessors and adding their annotated cheapest cost to the total.
     * <p>
     * After all nodes have been annotated, the algorithm will start the second phase, walking the graph from bottom
     * to top reducing the graph by substituting all nodes with their annotated cheapest rules. After substituting a
     * node, the algorithm determines the next predecessor node which has not been substituted yet and continues
     * execution there.
     * <p>
     * After all nodes have been substituted, the algorithm will enter the third phase, transforming the graph of
     * intermediate code operations that resulted from the second phase into one linear list of instructions.
     *
     * @return The linear list of operations
     */
    public BasicBlockGraph generateCode() {
        /*
         * ANNOTATE THE GRAPH
         * - Walk the DAG from the leaves (e.g. constants) to the roots (e.g. add) (Graph::walkPostorder)
         * - Annotate each node with the minimal cost of deriving the graph rooted at the current node
         *   - The minimal cost of the sub-nodes (predecessors) is already known
         *
         * REDUCE THE GRAPH
         * - Walk the DAG from the roots to the leaves (Graph::walkTopological)
         * - Substitute the nodes for their intermediate language code according to the annotated rule
         * - Go to the next node as determined by the applied rule
         *
         * See: M. Anton Ertl. Optimal code selection in DAGs. In Proceedings of the 26th ACM SIGPLAN-SIGACT
         * symposium on Principles of programming languages. ACM Press, 1999, S. 242–249.
         */

        // Remove the "Graph " prefix
        String graphName = graph.toString().substring(6);

        this.mode = Mode.BLOCKS;
        graph.walkBlocksPostorder(this);

        GraphDumper.dumpBlocksGraph(blocksGraph, graphName);

        /* ============================================= */
        /* 1. Create NodeAnnotations for every firm.Node */
        /* ============================================= */
        this.mode = Mode.ANNOTATION; // Set, in case generateCode() is called twice
        setPhiRules();
        graph.walkTopological(this);

        setJumpTargets();

        /* ===================================== */
        /* 2. Construct the NodeAnnotation graph */
        /* ===================================== */
        this.mode = Mode.CONSTRUCTION;
        graph.walkTopological(this);

        GraphDumper.dumpNodeAnnotationGraph(nodeAnnotationGraph, graphName, (Node n) -> annotations.get(n.getNr()));

        /* =========================================================== */
        /* 3. Transform the NodeAnnotation graph into a CodeNode graph */
        /* =========================================================== */
        this.mode = Mode.TRANSFORMATION;
        NodeAnnotation<?> start = annotations.get(graph.getStart().getNr());
        transformAnnotation(start);

        Iterator<NodeAnnotation<?>> nodeAnnotationGraphIterator = new BreadthFirstIterator<>(nodeAnnotationGraph);
        while (nodeAnnotationGraphIterator.hasNext()) {
            NodeAnnotation<?> next = nodeAnnotationGraphIterator.next();
            if (!next.getTransformed()) {
                System.out.println("New component of " + next.getRootNode().getBlock() + " starts at " + next.getRootNode());
                int compIdx = getFirmBlock(next.getRootNode().getBlock().getNr()).newComponent();
                next.setComponent(compIdx);
            }
            transformAnnotation(next);
        }

        GraphDumper.dumpCodeGraph(codeGraph, graphName);

        /* ======================================================== */
        /* 3. Linearize the graph by concatenating subsequent nodes */
        /* ======================================================== */
        this.mode = Mode.LINEARIZATION;

        // Before we go over the nodes in topological order, we have to remove all edges that span between blocks
        // Since we already transformed the nodes to code, we don't need the edges anymore
        List<DefaultEdge> removeEdges = codeGraph.edgeSet()
                .stream()
                .filter(e -> !Objects.equals(codeGraph.getEdgeSource(e).getFirmBlock().getId(),
                        codeGraph.getEdgeTarget(e).getFirmBlock().getId())).toList();
        removeEdges.forEach(codeGraph::removeEdge);

        // Do the linearization of the codeGraph

        Iterator<CodeNode> codeGraphIterator = new TopologicalOrderIterator<>(codeGraph);
        while (codeGraphIterator.hasNext()) {
            CodeNode next = codeGraphIterator.next();
            linearizeNode(next);
        }


        List<DefaultWeightedEdge> addEdges = new ArrayList<>();
        // remove, they are not needed anymore
        List<FirmBlock> removeBlocks = blocksGraph.vertexSet().stream().filter(b -> b.getId().startsWith("-")).toList();
        removeBlocks.forEach(b -> {
            FirmBlock succ = GraphUtil.getSuccessors(b, blocksGraph).get(0);
            FirmBlock pred = GraphUtil.getPredecessors(b, blocksGraph).get(0);
            blocksGraph.removeVertex(b);
            blocksGraph.addEdge(pred, succ);
        });

        // Remove unreachable blocks
        firmBlocks.values().forEach(v -> {
            if (!blocksGraph.containsVertex(v)) return;
            List<FirmBlock> jumpTargets = v.getJumpTargets().map(LabelOperand::getTarget).map(key -> {
                if (firmBlocks.containsKey(key)) return firmBlocks.get(key);
                else return firmBlocks.get(key.split("_")[0]);
            }).toList();
            List<DefaultWeightedEdge> edges = blocksGraph.incomingEdgesOf(v).stream().toList();

            for (DefaultWeightedEdge e : edges) {
                FirmBlock edgeSource = blocksGraph.getEdgeSource(e);
                if (!jumpTargets.contains(edgeSource)) {
                    blocksGraph.removeEdge(e);
                    if (blocksGraph.outDegreeOf(edgeSource) == 0) {
                        blocksGraph.removeVertex(edgeSource);
                    }
                }
            }
        });

        boolean progress;
        do {
            // remove unreachable blocks
            List<FirmBlock> trailingBlocks = firmBlocks.values().stream()
                    .filter(blocksGraph::containsVertex)
                    .filter(b -> blocksGraph.outDegreeOf(b) == 0)
                    .filter(b -> !b.getId().equals("" + graph.getStartBlock().getNr())).toList();
            progress = !trailingBlocks.isEmpty();
            blocksGraph.removeAllVertices(trailingBlocks);
        } while (progress);

        return new BasicBlockGraph(blocksGraph, graph.getEntity().getName());
    }

    private void setPhiRules() {
        graph.walk(new LazyNodeWalker() {
            @Override
            protected void visitAny(Node node) {
                if (node instanceof Phi phi && !Objects.equals(phi.getMode(), firm.Mode.getM())) {
                    boolean inRow = false;
                    for (int i = 0; i < phi.getPredCount(); i++) {
                        // only set for the last Phi in a row (in case of swapping)
                        if (phi.getPred(i) instanceof Phi) {
                            inRow = true;
                            break;
                        }
                    }
                    NodeAnnotation<Phi> a = new NodeAnnotation<>(1, phi, new PhiRule(), !inRow, false);
                    VirtualRegister target = new VirtualRegister();
                    target.setPhiVariable(true);
                    a.setDefinition(target);
                    annotations.put(phi.getNr(), a);

                }
            }
        });
    }

    private void setJumpTargets() {
        annotations.values().stream().filter(a -> a.getRule().needsJmpTarget()).forEach(a -> {
            String targetBlockId = jmpTargets.get(a.getRootNode());
            LabelOperand target = new LabelOperand(targetBlockId);
            a.setDefinition(target);

            if (a.getRule() instanceof CondJmpRule) {
                Proj projNode = (Proj) a.getRootNode();
                Cond cond = (Cond) projNode.getPred(0);
                CondTarget cndJmpTargets = (CondTarget) annotations.get(cond.getNr()).getDefinition();
                if (Objects.isNull(cndJmpTargets)) {
                    cndJmpTargets = new CondTarget();
                    annotations.get(cond.getNr()).setDefinition(cndJmpTargets);
                }
                switch (projNode.getNum()) {
                    case 0 -> cndJmpTargets.setFalseTarget(target);
                    case 1 -> cndJmpTargets.setTrueTarget(target);
                }
            }
        });
    }

    /**
     * Visits the given block, creating a new FirmBlock for it and adding it to the FirmBlock graph,
     * keeping the existing dependencies
     *
     * @param block The firm.nodes.Block to visit
     */
    @Override
    public void visitBlock(Block block) {
        FirmBlock fBlock = getOrCreateFirmBlock(block);
        // If we already visited the block, return
        if (fBlock.getVisited()) {
            return;
        }
        fBlock.setVisited(true);
        blocksGraph.addVertex(fBlock);

        for (int i = 0; i < block.getPredCount(); i++) {
            Node pred = block.getPred(i);
            FirmBlock predBlock = getOrCreateFirmBlock((Block) pred.getBlock());
            if (!blocksGraph.containsVertex(predBlock)) {
                visitBlock((Block) pred.getBlock());
            }

            // If the graph already contains an edge between these two nodes, add intermediate nodes
            if (blocksGraph.containsEdge(fBlock, predBlock)) {
                FirmBlock intermediate1 = new FirmBlock(nextIntermediateID());
                FirmBlock intermediate2 = new FirmBlock(nextIntermediateID());
                blocksGraph.addVertex(intermediate1);
                blocksGraph.addVertex(intermediate2);
                // Remove the existing edge and add the new edges
                double weight = blocksGraph.getEdgeWeight(blocksGraph.getEdge(fBlock, predBlock));
                blocksGraph.removeEdge(fBlock, predBlock);
                // Reconstruct the old edge with the old weight
                blocksGraph.addEdge(fBlock, intermediate1);
                blocksGraph.setEdgeWeight(fBlock, intermediate1, weight);
                blocksGraph.addEdge(intermediate1, predBlock);
                blocksGraph.setEdgeWeight(intermediate1, predBlock, weight);
                // Create the new edge with the new weight
                blocksGraph.addEdge(fBlock, intermediate2);
                blocksGraph.setEdgeWeight(fBlock, intermediate2, i);
                blocksGraph.addEdge(intermediate2, predBlock);
                blocksGraph.setEdgeWeight(intermediate2, predBlock, i);
            } else {

                DefaultWeightedEdge e = blocksGraph.addEdge(fBlock, predBlock);
                blocksGraph.setEdgeWeight(e, i);
            }
        }
    }

    /**
     * Returns or creates the FirmBlock for the given firm.nodes.Block and puts it into the global map firmBlocks
     *
     * @param block The block to transform into a FirmBlock
     */
    private FirmBlock getOrCreateFirmBlock(Block block) {
        if (!hasBlock(block.getNr())) {
            putBlock(block.getNr(), new FirmBlock(block));
        }
        return getFirmBlock(block.getNr());
    }

    private void putBlock(int id, FirmBlock fBlock) {
        firmBlocks.put(Integer.toString(id), fBlock);
    }

    private boolean hasBlock(int block) {
        return firmBlocks.containsKey(Integer.toString(block));
    }

    private FirmBlock getFirmBlock(int block) {
        return firmBlocks.get(Integer.toString(block));
    }


    /**
     * Returns or creates the FirmBlock for the given id and puts it into the global map firmBlocks
     *
     * @param id The id of the new FirmBlock
     */
    FirmBlock getOrCreateFirmBlock(int id) {
        if (!hasBlock(id)) {
            String strId = Integer.toString(id);
            firmBlocks.put(strId, new FirmBlock(strId));
        }
        return getFirmBlock(id);
    }

    /**
     * Creates a NodeAnnotation for the given node in the internal map.
     * Also calculates the optimal rule for transforming the given node.
     *
     * @param node The firm Node for which to create the annotation
     */
    private <T extends Node> void annotateNode(T node) {
        // We don't annotate basic blocks
        if (node instanceof Block block) {
            for (int i = 0; i < node.getPredCount(); i++) {
                Node jmp = node.getPred(i);
                String fmt = node.getPredCount() > 1 ? "%1$d_%2$d" : "%1$d";
                jmpTargets.put(jmp, fmt.formatted(block.getNr(), i));
            }
            return;
        }
        //noinspection unchecked
        Class<T> aClass = (Class<T>) node.getClass();

        // We only look at rules that have a root node that matches our current node
        RuleSet.forNodeClass(aClass, rule -> {
            // Check if rule matches the node and its predecessors
            if (rule.matches(node)) {
                rule.setNode(node);
                int cost = rule.getCost();

                // If we found a cheaper rule for that node (including all predecessors)
                if (!annotations.containsKey(node.getNr()) || annotations.get(node.getNr()).getCost() > cost) {
                    // Annotate the node
                    NodeAnnotation<T> a = this.createAnnotation(aClass, node, rule);
                    annotations.put(node.getNr(), a);
                }
                rule.clear();
            }
        });
        if (!annotations.containsKey(node.getNr())) {
            // If we have no rules to apply, create a dummy annotation
            // TODO: When all rules are specified this should not happen anymore!
            //  Every node needs at least one rule
            // Dummy annotation:
            NodeAnnotation<Node> a = this.createAnnotation(Node.class, node, new EmptyRule<>());
            annotations.put(node.getNr(), a);

            // Print a warning that there was no matching rule for the node
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                    .printWarning(CodeGenerationWarningIds.MISSING_RULE,
                            "No rule found that matches node nr. " + node.getNr() + ": " + node + ".");
        }


    }

    private <N extends Node> NodeAnnotation<N> createAnnotation(Class<N> nClass, N node, SubstitutionRule<N> rule) {
        return new NodeAnnotation<>(rule.getCost(), nClass.cast(node), rule);
    }

    /**
     * Inserts the NodeAnnotation for the specified node into the NodeAnnotation graph,
     * keeping the dependencies from the firm graph
     *
     * @param node The node for which to insert the NodeAnnotation
     */
    private <T extends Node> void constructNode(T node) {
        // We don't annotate basic blocks
        if (node instanceof Block) {
            return;
        }
        //noinspection unchecked
        NodeAnnotation<T> a = (NodeAnnotation<T>) annotations.get(node.getNr());
        assert a != null;
        // If we already visited the node (i.e. already transformed it using a rule in one of its successors), skip it
        if (a.getVisited()) {
            if (!nodeAnnotationGraph.containsVertex(a)) nodeAnnotationGraph.addVertex(a);
            for (Node pred : a.getRootNode().getPreds()) {
                addDependency(a, pred);
            }
            return;
        }
        SubstitutionRule<T> rule = a.getRule();
        // Mark the annotations of all required nodes visited, since they will be covered by this rule

        rule.setNode(node);
        for (Node n : rule.getRequiredNodes(graph)) {
            assert annotations.containsKey(n.getNr());
            annotations.get(n.getNr()).setVisited(true);
            firm.Mode mode = n.getMode();
            if (mode.equals(firm.Mode.getM()) || mode.equals(firm.Mode.getT())) {
                annotations.get(n.getNr()).clearRule();
            }
        }

        // Initialize targets
        rule.substitute();

        // Build a graph of NodeAnnotations

        // The vertex could already exist due to a dependency
        if (!nodeAnnotationGraph.containsVertex(a)) {
            nodeAnnotationGraph.addVertex(a);
        }
        for (Node pred : a.getRootNode().getPreds()) {
            if (a.getRootNode() instanceof Phi && a.getRootNode().getNr() < pred.getNr() && a.getRootNode().getBlock().equals(pred.getBlock())) {
                logger.printInfo("Attention: %s is part of an intra-block cycle!%n".formatted(a.getRootNode()));
                continue;
            }
            addDependency(a, pred);
        }
        // Maintain the dependencies inside the rule
        for (Node n : rule.getRequiredNodes(graph)) {
            // If one of our predecessors has a predecessor that has not been visited yet,
            // add it as an edge in the node annotation graph
            NodeAnnotation<?> predAnnotation = annotations.get(n.getNr());
            if (!predAnnotation.getVisited()) {
                addDependency(a, n);
            }
        }
        

        rule.clear();
    }

    /**
     * Creates a new edge from the given NodeAnnotation to the given predecessor firm Node.
     * If no vertex for the given firm Node exists, it will be created.
     *
     * @param root The NodeAnnotation from which the edge starts
     * @param pred The firm Node to which the dependency exists
     */
    private <T extends Node> void addDependency(NodeAnnotation<T> root, Node pred) {
        NodeAnnotation<?> predAnnotation = annotations.get(pred.getNr());
        if (!nodeAnnotationGraph.containsVertex(predAnnotation)) {
            nodeAnnotationGraph.addVertex(predAnnotation);
        }
        nodeAnnotationGraph.addEdge(root, predAnnotation);
    }

    /**
     * Applies the substitution rule stored in the given NodeAnnotation by transforming the annotation into a list of
     * Operations as specified by the rule, encapsulating it in a CodeNode object and adding that object to the code
     * graph, keeping its dependencies.
     *
     * @param a The NodeAnnotation to transform
     */
    private <T extends Node> void transformAnnotation(NodeAnnotation<T> a) {
        // If we already transformed this annotation (e.g. as a dependency of another node), skip it
        if (a.getTransformed()) {
            return;
        }

        a.setVisited(true);
        //System.out.println("tr " + a.getRootNode().toString());
        // Apply the rule in this annotation
        SubstitutionRule<T> rule = a.getRule();
        T rootNode = a.getRootNode();
        rule.setNode(rootNode);
        List<Operation> ops = rule.substitute();

        // Create the node in the code graph
        int blockNr = rootNode.getBlock().getNr();

        // Falls Phinode, werden hier mehr CodeNodes im Hintergrund erzeugt
        CodeNode codeNode;
        FirmBlock fBlock = getOrCreateFirmBlock(blockNr);
        if (a.getRule() instanceof PhiRule phiRule && phiRule.getDatatype() != Datatype.OTHER) {
            codeNode = createPhiNode(fBlock, phiRule);
        } else {
            codeNode = new CodeNode(ops, fBlock, rootNode.getNr());
            codeGraph.addVertex(codeNode);
        }
        codeNode.setComponent(a.getComponent());

        // Keep a map of the node ids for lookup
        codeGraphLookup.put(rootNode.getNr(), codeNode);

        // Get the predecessors (graph nodes that point to this node)
        List<? extends NodeAnnotation<?>> predecessors = GraphUtil.getPredecessors(a, nodeAnnotationGraph);
        List<Node> requiredNodes = a.getRule().getRequiredNodes(graph);
        rule.clear();
        
        requiredNodes
                .stream()
                .map(n -> annotations.get(n.getNr()))
                .flatMap(n -> GraphUtil.getPredecessors(n, nodeAnnotationGraph).stream())
                .distinct()
                // Transform the predecessors to its CodeNodes
                .map(pred -> {
                    if (!pred.getTransformed()) {
                        transformAnnotation(pred);
                    }
                    return codeGraphLookup.get(pred.getRootNode().getNr());
                })
                // Recreate the dependencies between the predecessors and the new CodeNode
                .forEach(pred -> codeGraph.addEdge(pred, codeNode));
        
        for (NodeAnnotation<?> p : predecessors) {
            int predNr = p.getRootNode().getNr();
            // If we have a predecessor that we did not transform yet, do it now, recursively
            if (p.getVisited()) {
                if (a.getRootNode().getBlock().equals(p.getRootNode().getBlock())) {
                    // Backpropagate component number
                    int actualCmp = p.getComponent();
                    codeNode.setComponent(actualCmp);
                    a.setComponent(actualCmp);
                }
            }

            if (!codeGraphLookup.containsKey(predNr)) {
                // node gets handled elsewhere and gets no own code
                boolean visited = p.getVisited();
                if (!visited) {
                    p.setComponent(a.getComponent());
                    if (!p.getRootNode().getBlock().equals(a.getRootNode().getBlock())) {
                        System.out.println("New component of " + p.getRootNode().getBlock() + " starts at " + p.getRootNode());
                        p.setComponent(getOrCreateFirmBlock(p.getRootNode().getBlock().getNr()).newComponent());
                    }
                    transformAnnotation(p);
                } else if (p.getRootNode().getMode().equals(firm.Mode.getM()) || p.getRootNode().getMode().equals(firm.Mode.getT())) {
                    // M nodes need to be traversed either way
                    transformAnnotation(p);
                    if (!(a.getRule() instanceof EmptyRule<T>))
                        codeGraph.addEdge(codeGraphLookup.get(memSuccessor.getNr()), codeNode);
                }
            }
        }

        // Mark all nodes that are covered by this rule as "transformed"
        // Transformed nodes' rules are not applied, but can still be used by rules from non-transformed nodes
        a.setTransformed(true);
        rule.setNode(rootNode);
        for (Node n : rule.getRequiredNodes(graph)) {
            // M-Nodes must be traversed either way.
            if (n.getMode().equals(firm.Mode.getM())) {
                continue;
            }
            annotations.get(n.getNr()).setTransformed(true);
        }
        rule.clear();

        firm.Mode mode = a.getRootNode().getMode();
        if ((mode.equals(firm.Mode.getM()) || mode.equals(firm.Mode.getT())) && !(a.getRule() instanceof EmptyRule<T>))
            memSuccessor = a.getRootNode();
    }

    /**
     * Creates a phi code node and code nodes for the phi blocks of fBlock.
     *
     * @param fBlock  the target firm block
     * @param phiRule the rule representing the phi node
     * @return the new (empty) phi node for the fBlock
     */
    private CodeNode createPhiNode(FirmBlock fBlock, PhiRule phiRule) {

        // This is not for Phi M nodes
        PhiNode node = new PhiNode(phiRule, fBlock);
        codeGraph.addVertex(node);
        List<FirmBlock> succBlocks = GraphUtil.getSuccessors(fBlock, blocksGraph);
        for (int i = 0; i < phiRule.getPredCount(); i++) {
            if (Objects.equals(phiRule.getRootNode().getMode(), firm.Mode.getM())) continue;
            Operation movOp = phiRule.getCodeForSucc(i);
            CodeNode succCode = new CodeNode(List.of(movOp), getOrCreatePhiBlock(fBlock, i, succBlocks.get(i)));
            codeGraphLookup.put(succCode.getId(), succCode);

            // Insert new phi code node in between this node and the original successor
            node.setCodeForPred(succCode, i);
            codeGraph.addVertex(succCode);
            codeGraph.addEdge(node, succCode);
        }

        return node;
    }

    private FirmBlock getOrCreatePhiBlock(FirmBlock target, int idx, FirmBlock oldSuccessorI) {
        String phiBlockId = target.getIdForPhiNode(idx);
        if (firmBlocks.containsKey(phiBlockId)) {
            return firmBlocks.get(phiBlockId);
        }

        FirmBlock phiBlock = new FirmBlock(target, idx);
        firmBlocks.put(phiBlockId, phiBlock);
        blocksGraph.addVertex(phiBlock);

        target.setPhiBlock(idx, phiBlock);

        //replace edge
        DefaultWeightedEdge oldEdge = blocksGraph.getEdge(target, oldSuccessorI);
        DefaultWeightedEdge newEdge0 = blocksGraph.addEdge(phiBlock, oldSuccessorI);
        DefaultWeightedEdge newEdge1 = blocksGraph.addEdge(target, phiBlock);
        blocksGraph.setEdgeWeight(newEdge0, blocksGraph.getEdgeWeight(oldEdge));
        blocksGraph.setEdgeWeight(newEdge1, blocksGraph.getEdgeWeight(oldEdge));
        blocksGraph.removeEdge(oldEdge);


        return phiBlock;
    }


    /**
     * Linearizes the given node by concatenating operations lists
     *
     * @param node The CodeNode to linearize
     */
    private void linearizeNode(CodeNode node) {
        // Since we traverse the graph in the opposite direction (result to operands),
        // we need to insert the operations at the beginning of the code list
        FirmBlock firmBlock = node.getFirmBlock();
        if (node.isPhi()) {
            //PhiNode contains code for left and right branch
            firmBlock.insertPhi((PhiNode) node);
        } else if (node.isCondition()) {
            firmBlock.setComparison(node);
        } else if (node.isJump()) {
            firmBlock.setJump(node);
        } else {
            if (node.getFirmBlock().getId().equals("111")) System.out.println("lin " + node);
            firmBlock.insertOperations(node);
        }
    }

    protected void visitAny(Node node) {
        // Do for any node
        switch (mode) {
            case ANNOTATION -> annotateNode(node);
            case CONSTRUCTION -> constructNode(node);
            default -> logger.internalError("Walking the firm graph using an unknown mode '" + mode.name() + "'");
        }
    }

}
