package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Graph;
import firm.nodes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeSelector implements NodeVisitor {
    
    private static final OutputMessageHandler logger = new OutputMessageHandler(MessageOrigin.CODE_GENERATION);
    
    // The current mode of the CodeSelector
    private enum Mode {
        ANNOTATION,
        REDUCTION,
        TRANSFORMATION
    }
    
    private final Graph graph;
    private final Map<Class<? extends Node>, List<SubstitutionRule>> rules;
    // Contains the NodeAnnotation of a given node nr. from the real graph
    private final Map<Integer, NodeAnnotation> annotations;
    private Mode mode;
    // TODO: Code Graph

    /**
     * Creates a new CodeSelector with the given Graph and SubstitutionRules
     * @param graph The graph to create intermediate code for
     * @param rules The map of rules to apply, keyed by the class of the root node
     */
    public CodeSelector(Graph graph, Map<Class<? extends Node>, List<SubstitutionRule>> rules) {
        this.graph = graph;
        this.rules = rules;
        this.annotations = new HashMap<>();
        this.mode = Mode.ANNOTATION;
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
         * Source: M. Anton Ertl. Optimal code selection in DAGs. In Proceedings of the 26th ACM SIGPLAN-SIGACT
         * symposium on Principles of programming languages. ACM Press, 1999, S. 242–249.
         */
        
        // Annotate the graph
        this.mode = Mode.ANNOTATION; // Set, in case generateCode() is called twice
        graph.walkPostorder(this);

        // Reduce the graph
        this.mode = Mode.REDUCTION;
        graph.walkTopological(this);
        
        // This mode switch is not really necessary, since we won't use graph.walk... to walk the graph and we don't
        // need to switch the mode for the NodeVisitor to work properly
        // TODO: Remove
        this.mode = Mode.TRANSFORMATION;
        List<Operation> operations = new ArrayList<>();
        // TODO: Walk the intermediate graph and generate a linear list of Operations
        
        return operations;
    }
    
    private void visitAny(Node node) {
        // Do for any node
        switch (mode) {
            case ANNOTATION -> annotateNode(node);
            case REDUCTION -> reduceNode(node);
        }
    }
    
    private void annotateNode(Node node) {
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
                        cost += predAnnotation.cost();
                    }
                }
                // If we found a cheaper rule for that node (including all predecessors)
                if (!annotations.containsKey(node.getNr()) || annotations.get(node.getNr()).cost() > cost) {
                    // Annotate the node
                    NodeAnnotation a = new NodeAnnotation(rule.getCost(), node, rule, false);
                    annotations.put(node.getNr(), a);
                }
            }
        }
    }
    
    private void reduceNode(Node node) {
        // Remove the annotation from the list, since we no longer need it
        NodeAnnotation a = annotations.remove(node.getNr());
        assert a != null;
        // Remove the annotations of all required nodes, since they will be covered by this rule
        // TODO: What if requirements are used by multiple parent nodes (visited flag)
        for (Node n : a.rule().getRequiredNodes(graph)) {
            assert annotations.containsKey(n.getNr());
            annotations.remove(n.getNr());
        }
        // Generate code for the nodes
        List<Operation> rootCode = a.rule().substitute(a.rootNode());
        for (Node n : a.rule().getRequiredNodes(graph)) {
            List<Operation> code = a.rule().substitute(n);
            // TODO: Set predecessor correctly?
        }
        
        // TODO: Append the generated code lists to the global code graph
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
