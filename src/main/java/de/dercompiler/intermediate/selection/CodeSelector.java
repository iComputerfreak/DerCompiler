package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.selection.rules.EmptyRule;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.GraphDumper;
import firm.nodes.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.*;

public class CodeSelector implements NodeVisitor {
    
    private static final OutputMessageHandler logger = new OutputMessageHandler(MessageOrigin.CODE_GENERATION);
    
    // TODO: Rename modes and functions
    // The current mode of the CodeSelector
    private enum Mode {
        ANNOTATION,
        REDUCTION,
        LINEARIZATION
    }
    
    private final firm.Graph graph;
    private final Map<Class<? extends Node>, List<SubstitutionRule>> rules;
    // Contains the NodeAnnotation of a given node nr. from the real graph
    private final Map<Integer, NodeAnnotation> annotations;
    private Mode mode;
    private final Graph<NodeAnnotation, DefaultEdge> nodeAnnotationGraph;
    private final Graph<CodeNode, DefaultEdge> codeGraph;
    private final List<Operation> operations;
    private final Map<Integer, CodeNode> codeGraphLookup;

    /**
     * Creates a new CodeSelector with the given Graph and SubstitutionRules
     * @param graph The graph to create intermediate code for
     * @param rules The map of rules to apply, keyed by the class of the root node
     */
    public CodeSelector(firm.Graph graph, Map<Class<? extends Node>, List<SubstitutionRule>> rules) {
        this.graph = graph;
        this.rules = rules;
        this.annotations = new HashMap<>();
        this.mode = Mode.ANNOTATION;
        this.nodeAnnotationGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        this.codeGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        this.operations = new ArrayList<>();
        this.codeGraphLookup = new HashMap<>();
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
    public List<Operation> generateCode() {
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
        
        // Annotate the graph
        this.mode = Mode.ANNOTATION; // Set, in case generateCode() is called twice
        graph.walkPostorder(this);

        // Transform the graph into a node annotation graph
        this.mode = Mode.REDUCTION;
        graph.walkTopological(this);

        GraphDumper.dumpNodeAnnotationGraph(nodeAnnotationGraph, graph.toString().substring(6));
        
        // This mode switch is not really necessary, since we won't use graph.walk... to walk the graph and we don't
        // need to switch the mode for the NodeVisitor to work properly
        this.mode = Mode.LINEARIZATION;
        Iterator<NodeAnnotation> it = new BreadthFirstIterator<>(nodeAnnotationGraph);
        while (it.hasNext()) {
            applySubstitutionRule(it.next());
        }
        
        GraphDumper.dumpCodeGraph(codeGraph, graph.toString().substring(6));
        
        return operations;
    }
    
    private void annotateNode(Node node) {
        if (!rules.containsKey(node.getClass())) {
            // If we have no rules to apply, we skip this node
            // TODO: When all rules are specified this should not happen anymore!
            //  Every node needs at least one rule
            // Dummy annotation:
            annotations.put(node.getNr(), new NodeAnnotation(0, node, new EmptyRule(node)));
            return;
        }
        // We only look at rules that have a root node that matches our current node
        for (SubstitutionRule rule : rules.get(node.getClass())) {
            // Check if rule matches the node and its predecessors
            if (SubstitutionRule.matches(rule.getRootNode(), node)) {
                int cost = rule.getCost();
                // We have to add the cost of all predecessors that are not affected by this rule
                for (Node p : node.getPreds()) {
                    // If the predecessor is not affected by this rule, add its minimal cost to the total
                    if (!rule.getRequiredNodes(graph).contains(p)) {
                        NodeAnnotation predAnnotation = annotations.get(p.getNr());
                        assert predAnnotation != null;
                        cost += predAnnotation.getCost();
                    }
                }
                // If we found a cheaper rule for that node (including all predecessors)
                if (!annotations.containsKey(node.getNr()) || annotations.get(node.getNr()).getCost() > cost) {
                    // Annotate the node
                    NodeAnnotation a = new NodeAnnotation(rule.getCost(), node, rule);
                    annotations.put(node.getNr(), a);
                }
            }
        }
    }
    
    private void reduceNode(Node node) {
        NodeAnnotation a = annotations.get(node.getNr());
        assert a != null;
        // If we already visited the node (i.e. already transformed it using a rule in one of its successors), skip it
        if (a.getVisited()) return;
        // Mark the annotations of all required nodes visited, since they will be covered by this rule
        for (Node n : a.getRule().getRequiredNodes(graph)) {
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
        for (Node n : a.getRule().getRequiredNodes(graph)) {
            // If one of our predecessors has a predecessor that has not been visited yet,
            // add it as an edge in the node annotation graph
            NodeAnnotation predAnnotation = annotations.get(n.getNr());
            if (!predAnnotation.getVisited()) {
                addDependency(a, n);
            }
        }
    }
    
    private void addDependency(NodeAnnotation root, Node pred) {
        NodeAnnotation predAnnotation = annotations.get(pred.getNr());
        if (!nodeAnnotationGraph.containsVertex(predAnnotation)) {
            nodeAnnotationGraph.addVertex(predAnnotation);
        }
        nodeAnnotationGraph.addEdge(root, predAnnotation);
    }
    
    private void applySubstitutionRule(NodeAnnotation a) {
        // If we already transformed this annotation (e.g. as a dependency of another node), skip it
        if (a.getTransformed()) {
            return;
        }
        // Get the predecessors (graph nodes that point to this node)
        List<NodeAnnotation> predecessors = nodeAnnotationGraph.incomingEdgesOf(a).stream()
                .map(nodeAnnotationGraph::getEdgeSource).toList();
        // Apply the rule in this annotation
        List<Operation> ops = a.getRule().substitute(a.getRootNode());
        
        // Create the node in the code graph
        CodeNode codeNode = new CodeNode(ops);
        codeGraph.addVertex(codeNode);
        // Keep a map of the node ids for lookup
        codeGraphLookup.put(a.getRootNode().getNr(), codeNode);
        
        for (NodeAnnotation p : predecessors) {
            int predNr = p.getRootNode().getNr();
            // If we have a predecessor that we did not transform yet, do it now, recursively
            if (!codeGraphLookup.containsKey(predNr)) {
                applySubstitutionRule(annotations.get(predNr));
            }
            CodeNode predNode = codeGraphLookup.get(predNr);
            // Recreate the dependency
            codeGraph.addEdge(predNode, codeNode);
        }
        
        // Mark all nodes that are covered by this rule as "transformed"
        // Transformed nodes' rules are not applied, but can still be used by rules from non-transformed nodes
        a.setTransformed(true);
        for (Node n : a.getRule().getRequiredNodes(graph)) {
            annotations.get(n.getNr()).setTransformed(true);
        }
    }
    
    // TODO: Use NodeAnnotation::substitute instead and just call it in the linearize phase?
    private void linearizeNode(NodeAnnotation a) {
        // TODO: Transform all NodeAnnotations into code and add it to the operations list
        // TODO: Maybe keep using a bfs style to go over the required nodes?
        for (Node n : a.getRule().getRequiredNodes(graph)) {
            List<Operation> code = a.getRule().substitute(n);
        }
        List<Operation> rootCode = a.getRule().substitute(a.getRootNode());
        
        // TODO: Add operations to the global operations list
        // Preserve the correct order and linearize the graph
        // operations.add(op);
    }

    private void visitAny(Node node) {
        // Do for any node
        switch (mode) {
            case ANNOTATION -> annotateNode(node);
            case REDUCTION -> reduceNode(node);
            default -> logger.internalError("Walking the graph using an unknown mode '" + mode.name() + "'");
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
