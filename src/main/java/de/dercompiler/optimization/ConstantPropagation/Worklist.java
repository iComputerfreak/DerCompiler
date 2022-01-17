package de.dercompiler.optimization.ConstantPropagation;

import firm.Graph;
import firm.TargetValue;
import firm.nodes.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Worklist {

    private static final TargetValue UNKNOWN = TargetValue.getUnknown();
    private static final TargetValue BAD = TargetValue.getBad();

    private TransferFunctionVisitor transferFunctionVisitor;
    private Queue<Node> nodeQueue;
    private HashMap<Node, List<Node>> successorsMap;
    private HashMap<Node, TargetValue> targetValueMap;
    private Graph graph;

    public Worklist(ITransferFunction transferFunction, Graph graph){
        nodeQueue = new LinkedList<Node>();
        successorsMap = new HashMap<Node, List<Node>>();
        targetValueMap = new HashMap<Node, TargetValue>();
        transferFunction.setTargetValues(targetValueMap);
        transferFunctionVisitor = new TransferFunctionVisitor(transferFunction, nodeQueue, successorsMap, targetValueMap);
        this.graph = graph;
    }

    public static void run(ITransferFunction transferFunction, Graph graph){
        Worklist worklist = new Worklist(transferFunction, graph);
        worklist.work();
    }

    private void work(){

        graph.walkTopological(new NodeVisitor() {
            private void doAlways(Node node){
                for (Node pred: node.getPreds()){
                    List<Node> list;
                    if (successorsMap.containsKey(pred)){
                        list = successorsMap.get(pred);
                    } else {
                        list = new LinkedList<Node>();
                        successorsMap.put(pred, list);
                    }
                    list.add(node);
                }
                targetValueMap.put(node, UNKNOWN);
                nodeQueue.add(node);

            }

            @Override
            public void visit(Add node) {doAlways(node);}
            @Override
            public void visit(Address node) {doAlways(node);}
            @Override
            public void visit(Align node)  {doAlways(node);}
            @Override
            public void visit(Alloc node) {doAlways(node);}
            @Override
            public void visit(Anchor node) {doAlways(node);}
            @Override
            public void visit(And node) {doAlways(node);}
            @Override
            public void visit(Bad node) {doAlways(node);}
            @Override
            public void visit(Bitcast node) {doAlways(node);}
            @Override
            public void visit(Block node) {doAlways(node);}
            @Override
            public void visit(Builtin node) {doAlways(node);}
            @Override
            public void visit(Call node) {doAlways(node);}
            @Override
            public void visit(Cmp node) {doAlways(node);}
            @Override
            public void visit(Cond node) {doAlways(node);}
            @Override
            public void visit(Confirm node) {doAlways(node);}
            @Override
            public void visit(Const node) {doAlways(node);}
            @Override
            public void visit(Conv node) {doAlways(node);}
            @Override
            public void visit(CopyB node) {doAlways(node);}
            @Override
            public void visit(Deleted node)  {doAlways(node);}
            @Override
            public void visit(Div node) {doAlways(node);}
            @Override
            public void visit(Dummy node) {doAlways(node);}
            @Override
            public void visit(End node) {doAlways(node);}
            @Override
            public void visit(Eor node) {doAlways(node);}
            @Override
            public void visit(Free node) {doAlways(node);}
            @Override
            public void visit(IJmp node) {doAlways(node);}
            @Override
            public void visit(Id node) {doAlways(node);}
            @Override
            public void visit(Jmp node)  {doAlways(node);}
            @Override
            public void visit(Load node) {doAlways(node);}
            @Override
            public void visit(Member node) {doAlways(node);}
            @Override
            public void visit(Minus node) {doAlways(node);}
            @Override
            public void visit(Mod node)  {doAlways(node);}
            @Override
            public void visit(Mul node)  {doAlways(node);}
            @Override
            public void visit(Mulh node) {doAlways(node);}
            @Override
            public void visit(Mux node) {doAlways(node);}
            @Override
            public void visit(NoMem node) {doAlways(node);}
            @Override
            public void visit(Not node) {doAlways(node);}
            @Override
            public void visit(Offset node) {doAlways(node);}
            @Override
            public void visit(Or node) {doAlways(node);}
            @Override
            public void visit(Phi node) {doAlways(node);}
            @Override
            public void visit(Pin node) {doAlways(node);}
            @Override
            public void visit(Proj node) {doAlways(node);}
            @Override
            public void visit(Raise node) {doAlways(node);}
            @Override
            public void visit(Return node) {doAlways(node);}
            @Override
            public void visit(Sel node) {doAlways(node);}
            @Override
            public void visit(Shl node) {doAlways(node);}
            @Override
            public void visit(Shr node) {doAlways(node);}
            @Override
            public void visit(Shrs node) {doAlways(node);}
            @Override
            public void visit(Size node) {doAlways(node);}
            @Override
            public void visit(Start node) {doAlways(node);}
            @Override
            public void visit(Store node) {doAlways(node);}
            @Override
            public void visit(Sub node) {doAlways(node);}
            @Override
            public void visit(Switch node) {doAlways(node);}
            @Override
            public void visit(Sync node) {doAlways(node);}
            @Override
            public void visit(Tuple node) {doAlways(node);}
            @Override
            public void visit(Unknown node) {doAlways(node);}
            @Override
            public void visitUnknown(Node node) {}
        });

        while (!nodeQueue.isEmpty()){
            Node node = nodeQueue.poll();
            node.accept(transferFunctionVisitor);
        }

        graph.walkTopological(new NodeVisitor() {
            private void doAlways(Node node){
                TargetValue value = targetValueMap.get(node);
                if (!(value.equals(BAD) || value.equals(UNKNOWN))){
                    Graph.exchange(node, graph.newConst(value));
                }
            }

            @Override
            public void visit(Add node) {doAlways(node);}
            @Override
            public void visit(Address node) {doAlways(node);}
            @Override
            public void visit(Align node)  {doAlways(node);}
            @Override
            public void visit(Alloc node) {doAlways(node);}
            @Override
            public void visit(Anchor node) {doAlways(node);}
            @Override
            public void visit(And node) {doAlways(node);}
            @Override
            public void visit(Bad node) {doAlways(node);}
            @Override
            public void visit(Bitcast node) {doAlways(node);}
            @Override
            public void visit(Block node) {doAlways(node);}
            @Override
            public void visit(Builtin node) {doAlways(node);}
            @Override
            public void visit(Call node) {doAlways(node);}
            @Override
            public void visit(Cmp node) {doAlways(node);}
            @Override
            public void visit(Cond node) {doAlways(node);}
            @Override
            public void visit(Confirm node) {doAlways(node);}
            @Override
            public void visit(Const node) {doAlways(node);}
            @Override
            public void visit(Conv node) {doAlways(node);}
            @Override
            public void visit(CopyB node) {doAlways(node);}
            @Override
            public void visit(Deleted node)  {doAlways(node);}
            @Override
            public void visit(Div node) {doAlways(node);}
            @Override
            public void visit(Dummy node) {doAlways(node);}
            @Override
            public void visit(End node) {doAlways(node);}
            @Override
            public void visit(Eor node) {doAlways(node);}
            @Override
            public void visit(Free node) {doAlways(node);}
            @Override
            public void visit(IJmp node) {doAlways(node);}
            @Override
            public void visit(Id node) {doAlways(node);}
            @Override
            public void visit(Jmp node)  {doAlways(node);}
            @Override
            public void visit(Load node) {doAlways(node);}
            @Override
            public void visit(Member node) {doAlways(node);}
            @Override
            public void visit(Minus node) {doAlways(node);}
            @Override
            public void visit(Mod node)  {doAlways(node);}
            @Override
            public void visit(Mul node)  {doAlways(node);}
            @Override
            public void visit(Mulh node) {doAlways(node);}
            @Override
            public void visit(Mux node) {doAlways(node);}
            @Override
            public void visit(NoMem node) {doAlways(node);}
            @Override
            public void visit(Not node) {doAlways(node);}
            @Override
            public void visit(Offset node) {doAlways(node);}
            @Override
            public void visit(Or node) {doAlways(node);}
            @Override
            public void visit(Phi node) {doAlways(node);}
            @Override
            public void visit(Pin node) {doAlways(node);}
            @Override
            public void visit(Proj node) {doAlways(node);}
            @Override
            public void visit(Raise node) {doAlways(node);}
            @Override
            public void visit(Return node) {doAlways(node);}
            @Override
            public void visit(Sel node) {doAlways(node);}
            @Override
            public void visit(Shl node) {doAlways(node);}
            @Override
            public void visit(Shr node) {doAlways(node);}
            @Override
            public void visit(Shrs node) {doAlways(node);}
            @Override
            public void visit(Size node) {doAlways(node);}
            @Override
            public void visit(Start node) {doAlways(node);}
            @Override
            public void visit(Store node) {doAlways(node);}
            @Override
            public void visit(Sub node) {doAlways(node);}
            @Override
            public void visit(Switch node) {doAlways(node);}
            @Override
            public void visit(Sync node) {doAlways(node);}
            @Override
            public void visit(Tuple node) {doAlways(node);}
            @Override
            public void visit(Unknown node) {doAlways(node);}
            @Override
            public void visitUnknown(Node node) {}
        });
    }
}
