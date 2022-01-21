package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.rules.EmptyRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.GraphDumper;
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
import java.util.stream.Collectors;

public class CodeSelector implements NodeVisitor, BlockWalker {
    
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
    private final RuleSet rules;
    // Contains the NodeAnnotation of a given node nr. from the real graph
    private final Map<Integer, NodeAnnotation<?>> annotations = new HashMap<>();
    private Mode mode = Mode.BLOCKS;
    private final Graph<NodeAnnotation<?>, DefaultEdge> nodeAnnotationGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private final Graph<CodeNode, DefaultEdge> codeGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private final Graph<FirmBlock, DefaultWeightedEdge> blocksGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
    private final Map<Integer, CodeNode> codeGraphLookup = new HashMap<>();
    private final HashMap<Integer, FirmBlock> firmBlocks = new HashMap<>();
    private int nextIntermediateID = -1;

    /**
     * Creates a new CodeSelector with the given Graph and SubstitutionRules
     * @param graph The graph to create intermediate code for
     * @param rules The map of rules to apply, keyed by the class of the root node
     */
    public CodeSelector(firm.Graph graph, RuleSet rules) {
        this.graph = graph;
        this.rules = rules;
    }
    
    /**
     * Returns the next free ID to be used for the intermediate blocks in the FirmBlock graph
     */
    private int nextIntermediateID() {
        return nextIntermediateID--;
    }

    /**
     * Generates intermediate code for the Graph that was given when creating this class
     * using the set of rules that was given.
     * 
     * The algorithm will run in 3 phases.
     * 
     * In the first phase, the algorithm will annotate the firm graph, walking it from top to bottom
     * (i.e. from {@link firm.nodes.Start} to {@link firm.nodes.End}), annotating each node with the cheapest rule that
     * can be used to substitute this node (potentially including some or all predecessors), taking into consideration
     * any left-out predecessors and adding their annotated cheapest cost to the total.
     * 
     * After all nodes have been annotated, the algorithm will start the second phase, walking the graph from bottom
     * to top reducing the graph by substituting all nodes with their annotated cheapest rules. After substituting a
     * node, the algorithm determines the next predecessor node which has not been substituted yet and continues
     * execution there.
     * 
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
        graph.walkPostorder(this);

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
        Iterator<NodeAnnotation<?>> nodeAnnotationGraphIterator = new BreadthFirstIterator<>(nodeAnnotationGraph);
        while (nodeAnnotationGraphIterator.hasNext()) {
            transformAnnotation(nodeAnnotationGraphIterator.next());
        }

        GraphDumper.dumpCodeGraph(codeGraph, graphName);

        /* ======================================================== */
        /* 3. Linearize the graph by concatenating subsequent nodes */
        /* ======================================================== */
        this.mode = Mode.LINEARIZATION;
        
        // Before we go over the nodes in topological order, we have to remove all edges that span between blocks
        // Since we already transformed the nodes to code, we don't need the edges anymore
        List<DefaultEdge> edgesToRemove = codeGraph.edgeSet()
                .stream()
                .filter(e -> codeGraph.getEdgeSource(e).getFirmBlock().getNr() !=
                        codeGraph.getEdgeTarget(e).getFirmBlock().getNr())
                .collect(Collectors.toList());
        edgesToRemove.forEach(codeGraph::removeEdge);
        
        // Do the linearization of the codeGraph
        Iterator<CodeNode> codeGraphIterator = new TopologicalOrderIterator<>(codeGraph);
        // TODO: Remove after real rules are implemented
        Map<Integer, List<CodeNode>> linearizedCodeNodes = new HashMap<>();
        while (codeGraphIterator.hasNext()) {
            CodeNode next = codeGraphIterator.next();
            linearizeNode(next);
            
            // TODO: Remove after real rules are implemented
            // Create a debug graph with the linearized nodes
            if (GraphDumper.dump_graph) {
                int idx = next.getFirmBlock().getNr();
                linearizedCodeNodes.computeIfAbsent(idx, k -> new LinkedList<>());
                linearizedCodeNodes.get(idx).add(next);
            }
        }
        
        // TODO: Remove after real rules are implemented
        if (GraphDumper.dump_graph) {
            for (int nr : linearizedCodeNodes.keySet()) {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION)
                        .debugPrint("FirmBlock<" + nr + ">: " +
                                // List of all node numbers
                                String.join(", ", linearizedCodeNodes.get(nr).stream()
                                .map(n -> Integer.toString(n.getId())).toList()));
            }
        }
        
        // TODO: is getEntity().getName() the method name?
        return new BasicBlockGraph(blocksGraph, graph.getEntity().getName());
    }

    /**
     * Visits the given block, creating a new FirmBlock for it and adding it to the FirmBlock graph,
     * keeping the existing dependencies
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
     * @param block The block to transform into a FirmBlock
     */
    private FirmBlock getOrCreateFirmBlock(Block block) {
        if (!firmBlocks.containsKey(block.getNr())) {
            firmBlocks.put(block.getNr(), new FirmBlock(block));
        }
        return firmBlocks.get(block.getNr());
    }

    /**
     * Creates a NodeAnnotation for the given node in the internal map.
     * Also calculates the optimal rule for transforming the given node.
     * @param node The firm Node for which to create the annotation
     */
    private <T extends Node> void annotateNode(T node) {
        // We don't annotate basic blocks
        if (node instanceof Block) {
            return;
        }
        Class<T> aClass = (Class<T>) node.getClass();
        if (!RuleSet.containsKey(aClass)) {
            // If we have no rules to apply, create a dummy annotation
            // TODO: When all rules are specified this should not happen anymore!
            //  Every node needs at least one rule
            // Dummy annotation:
            annotations.put(node.getNr(), new NodeAnnotation<Node>(0, node, new EmptyRule()));
            return;
        }
        // We only look at rules that have a root node that matches our current node
        RuleSet.forNodeClass(aClass, rule -> {
            // Check if rule matches the node and its predecessors
            if (rule.matches(node)) {
                int cost = rule.getCost();

                // If we found a cheaper rule for that node (including all predecessors)
                if (!annotations.containsKey(node.getNr()) || annotations.get(node.getNr()).getCost() > cost) {
                    // Annotate the node
                    NodeAnnotation<T> a = this.createAnnotation(aClass, node, rule);
                    annotations.put(node.getNr(), a);
                }
            }
        });
        if (!annotations.containsKey(node.getNr())) {
            NodeAnnotation<Node> a = this.createAnnotation(Node.class, node, new EmptyRule());
            annotations.put(node.getNr(), a);
        }

    }

    private <N extends Node> NodeAnnotation<N> createAnnotation(Class<N> nClass, Node node, SubstitutionRule<N> rule) {
        return new NodeAnnotation<N>(rule.getCost(), nClass.cast(node), rule);
    }
    /**
     * Inserts the NodeAnnotation for the specified node into the NodeAnnotation graph,
     * keeping the dependencies from the firm graph
     * @param node The node for which to insert the NodeAnnotation
     */
    private <T extends Node> void constructNode(T node) {
        // We don't annotate basic blocks
        if (node instanceof Block) {
            return;
        }
        NodeAnnotation<T> a = (NodeAnnotation<T>) annotations.get(node.getNr());
        assert a != null;
        // If we already visited the node (i.e. already transformed it using a rule in one of its successors), skip it
        if (a.getVisited()) return;
        SubstitutionRule<T> rule = a.getRule();
        // Mark the annotations of all required nodes visited, since they will be covered by this rule

        rule.setAnnotations(a, (Node n) -> annotations.get(n.getNr()));
        for (Node n : rule.getRequiredNodes(graph)) {
            assert annotations.containsKey(n.getNr());
            annotations.get(n.getNr()).setVisited(true);
        }
        
        // Build a graph of NodeAnnotations
        
        // The vertex could already exist due to a dependency
        if (!nodeAnnotationGraph.containsVertex(a)) {
            nodeAnnotationGraph.addVertex(a);
        }
        for (Node pred : a.getRootNode().getPreds()) {
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
        rule.clearAnnotations();
    }

    /**
     * Creates a new edge from the given NodeAnnotation to the given predecessor firm Node.
     * If no vertex for the given firm Node exists, it will be created.
     * 
     * @param root The NodeAnnotation from which the edge starts
     * @param pred The firm Node to which the dependency exists
     */
    private <T extends Node> void addDependency(NodeAnnotation<T> root, Node pred) {
        NodeAnnotation<T> predAnnotation = (NodeAnnotation<T>) annotations.get(pred.getNr());
        if (!nodeAnnotationGraph.containsVertex(predAnnotation)) {
            nodeAnnotationGraph.addVertex(predAnnotation);
        }
        nodeAnnotationGraph.addEdge(root, predAnnotation);
    }

    /**
     * Applies the substitution rule stored in the given NodeAnnotation by transforming the annotation into a list of
     * Operations as specified by the rule, encapsulating it in a CodeNode object and adding that object to the code
     * graph, keeping its dependencies.
     * @param a The NodeAnnotation to transform
     */
    private <T extends Node> void transformAnnotation(NodeAnnotation<T> a) {
        // If we already transformed this annotation (e.g. as a dependency of another node), skip it
        if (a.getTransformed()) {
            return;
        }
        // Get the predecessors (graph nodes that point to this node)
        List<? extends NodeAnnotation<?>> predecessors = nodeAnnotationGraph.incomingEdgesOf(a).stream()
                .map(nodeAnnotationGraph::getEdgeSource).toList();
        // Apply the rule in this annotation
        SubstitutionRule<T> rule = a.getRule();
        rule.setAnnotations(a, (Node n) -> annotations.get(n.getNr()));
        List<Operation> ops = rule.substitute();

        // Create the node in the code graph
        int blockNr = a.getRootNode().getBlock().getNr();
        CodeNode codeNode = new CodeNode(ops, firmBlocks.get(blockNr), rule.getRootNode() instanceof Phi);
        codeGraph.addVertex(codeNode);
        // Keep a map of the node ids for lookup
        codeGraphLookup.put(a.getRootNode().getNr(), codeNode);
        rule.clearAnnotations();

        for (NodeAnnotation<?> p : predecessors) {
            int predNr = p.getRootNode().getNr();
            // If we have a predecessor that we did not transform yet, do it now, recursively
            if (!codeGraphLookup.containsKey(predNr)) {
                // node gets handled elsewhere and gets no own code
                if (annotations.get(predNr).getVisited()) continue;

                transformAnnotation(annotations.get(predNr));
            }
            CodeNode predNode = codeGraphLookup.get(predNr);
            // Recreate the dependency
            codeGraph.addEdge(predNode, codeNode);
        }
        
        // Mark all nodes that are covered by this rule as "transformed"
        // Transformed nodes' rules are not applied, but can still be used by rules from non-transformed nodes
        a.setTransformed(true);
        rule.setAnnotations(a, (Node n) -> annotations.get(n.getNr()));
        for (Node n : rule.getRequiredNodes(graph)) {
            annotations.get(n.getNr()).setTransformed(true);
        }
        rule.clearAnnotations();
    }

    /**
     * Linearizes the given node by concatenating operations lists
     * @param node The CodeNode to linearize
     */
    private void linearizeNode(CodeNode node) {
        if (node.isPhi()) {
            node.getFirmBlock().addPhi(node);
        } else {
            node.getFirmBlock().addOperations(node.getOperations());
        }
    }

    private void visitAny(Node node) {
        // Do for any node
        switch (mode) {
            case ANNOTATION -> annotateNode(node);
            case CONSTRUCTION -> constructNode(node);
            default -> logger.internalError("Walking the firm graph using an unknown mode '" + mode.name() + "'");
        }
    }

    @Override
    public void visit(Add add) {
        visitAny(add);
    }

    @Override
    public void visit(Address address) {
        visitAny(address);
    }

    @Override
    public void visit(Align align) {
        visitAny(align);
    }

    @Override
    public void visit(Alloc alloc) {
        visitAny(alloc);
    }

    @Override
    public void visit(Anchor anchor) {
        visitAny(anchor);
    }

    @Override
    public void visit(And and) {
        visitAny(and);
    }

    @Override
    public void visit(Bad bad) {
        visitAny(bad);
    }

    @Override
    public void visit(Bitcast bitcast) {
        visitAny(bitcast);
    }

    @Override
    public void visit(Block block) {
        visitAny(block);
    }

    @Override
    public void visit(Builtin builtin) {
        visitAny(builtin);
    }

    @Override
    public void visit(Call call) {
        visitAny(call);
    }

    @Override
    public void visit(Cmp cmp) {
        visitAny(cmp);
    }

    @Override
    public void visit(Cond cond) {
        visitAny(cond);
    }

    @Override
    public void visit(Confirm confirm) {
        visitAny(confirm);
    }

    @Override
    public void visit(Const aConst) {
        visitAny(aConst);
    }

    @Override
    public void visit(Conv conv) {
        visitAny(conv);
    }

    @Override
    public void visit(CopyB copyB) {
        visitAny(copyB);
    }

    @Override
    public void visit(Deleted deleted) {
        visitAny(deleted);
    }

    @Override
    public void visit(Div div) {
        visitAny(div);
    }

    @Override
    public void visit(Dummy dummy) {
        visitAny(dummy);
    }

    @Override
    public void visit(End end) {
        visitAny(end);
    }

    @Override
    public void visit(Eor eor) {
        visitAny(eor);
    }

    @Override
    public void visit(Free free) {
        visitAny(free);
    }

    @Override
    public void visit(IJmp iJmp) {
        visitAny(iJmp);
    }

    @Override
    public void visit(Id id) {
        visitAny(id);
    }

    @Override
    public void visit(Jmp jmp) {
        visitAny(jmp);
    }

    @Override
    public void visit(Load load) {
        visitAny(load);
    }

    @Override
    public void visit(Member member) {
        visitAny(member);
    }

    @Override
    public void visit(Minus minus) {
        visitAny(minus);
    }

    @Override
    public void visit(Mod mod) {
        visitAny(mod);
    }

    @Override
    public void visit(Mul mul) {
        visitAny(mul);
    }

    @Override
    public void visit(Mulh mulh) {
        visitAny(mulh);
    }

    @Override
    public void visit(Mux mux) {
        visitAny(mux);
    }

    @Override
    public void visit(NoMem noMem) {
        visitAny(noMem);
    }

    @Override
    public void visit(Not not) {
        visitAny(not);
    }

    @Override
    public void visit(Offset offset) {
        visitAny(offset);
    }

    @Override
    public void visit(Or or) {
        visitAny(or);
    }

    @Override
    public void visit(Phi phi) {
        visitAny(phi);
    }

    @Override
    public void visit(Pin pin) {
        visitAny(pin);
    }

    @Override
    public void visit(Proj proj) {
        visitAny(proj);
    }

    @Override
    public void visit(Raise raise) {
        visitAny(raise);
    }

    @Override
    public void visit(Return aReturn) {
        visitAny(aReturn);
    }

    @Override
    public void visit(Sel sel) {
        visitAny(sel);
    }

    @Override
    public void visit(Shl shl) {
        visitAny(shl);
    }

    @Override
    public void visit(Shr shr) {
        visitAny(shr);
    }

    @Override
    public void visit(Shrs shrs) {
        visitAny(shrs);
    }

    @Override
    public void visit(Size size) {
        visitAny(size);
    }

    @Override
    public void visit(Start start) {
        visitAny(start);
    }

    @Override
    public void visit(Store store) {
        visitAny(store);
    }

    @Override
    public void visit(Sub sub) {
        visitAny(sub);
    }

    @Override
    public void visit(Switch aSwitch) {
        visitAny(aSwitch);
    }

    @Override
    public void visit(Sync sync) {
        visitAny(sync);
    }

    @Override
    public void visit(Tuple tuple) {
        visitAny(tuple);
    }

    @Override
    public void visit(Unknown unknown) {
        visitAny(unknown);
    }

    @Override
    public void visitUnknown(Node node) {
        visitAny(node);
    }
}
