package de.dercompiler.optimization.ConstantPropagation;

import de.dercompiler.intermediate.selection.LazyNodeWalker;
import firm.*;
import firm.nodes.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Worklist {

    public static final TargetValue UNKNOWN = TargetValue.getUnknown();
    public static final TargetValue BAD = TargetValue.getBad();

    private TransferFunctionVisitor transferFunctionVisitor;
    private Queue<Node> nodeQueue;
    private HashMap<Node, List<Node>> successorsMap;
    private HashMap<Integer, TargetValue> targetValueMap;
    private Graph graph;

    public Worklist(ITransferFunction transferFunction, Graph graph) {
        nodeQueue = new LinkedList<>();
        successorsMap = new HashMap<>();
        targetValueMap = new HashMap<>();
        transferFunction.setTargetValues(targetValueMap);
        transferFunctionVisitor = new TransferFunctionVisitor(transferFunction, nodeQueue, successorsMap, targetValueMap);
        this.graph = graph;
    }

    public static void run(ITransferFunction transferFunction, Graph graph) {
        Worklist worklist = new Worklist(transferFunction, graph);
        worklist.work();
    }

    private void work() {

        graph.walkTopological(new WorklistAnalysisWalker());

        while (!nodeQueue.isEmpty()) {
            Node node = nodeQueue.poll();
            node.accept(transferFunctionVisitor);
        }

        graph.walkTopological(new WorklistReplaceWalker());
        BackEdges.disable(graph);
    }

    private class WorklistReplaceWalker extends LazyNodeWalker {

        private void replaceDivOrMod(Node node, Node previousMemory, Node replacement) {
            if (!BackEdges.enabled(graph)) {
                BackEdges.enable(graph);
            }
            for (BackEdges.Edge out : BackEdges.getOuts(node)) {
                if (out.node.getMode().equals(Mode.getM())) {
                    Graph.exchange(out.node, previousMemory);
                } else {
                    Graph.exchange(out.node, replacement);
                }
            }
        }

        protected void visitAny(Node node) {
            TargetValue value = targetValueMap.get(node.getNr());

            if (value != null && !(value.equals(BAD) || value.equals(UNKNOWN))) {
                if (node instanceof Div div) {
                    replaceDivOrMod(div, div.getMem(), graph.newConst(value));
                } else if (node instanceof Cmp cmp) {
                    Relation oldRel = cmp.getRelation();
                    if (oldRel.equals(Relation.True) || oldRel.equals(Relation.False)) return;
                    Relation newRel = value.asInt() == 1 ? Relation.True : Relation.False;
                    Graph.exchange(node, graph.newCmp(node.getBlock(), cmp.getLeft(), cmp.getLeft(), newRel));
                } else if (!(node instanceof Const)) {
                    Graph.exchange(node, graph.newConst(value));
                }
            }

        }

    }

    private class WorklistAnalysisWalker extends LazyNodeWalker {

            protected void visitAny(Node node) {
                for (Node pred : node.getPreds()) {
                    List<Node> list;
                    if (successorsMap.containsKey(pred)) {
                        list = successorsMap.get(pred);
                    } else {
                        list = new LinkedList<>();
                        successorsMap.put(pred, list);
                    }
                    list.add(node);
                }
                targetValueMap.put(node.getNr(), UNKNOWN);
                nodeQueue.add(node);

            }


        }

}



