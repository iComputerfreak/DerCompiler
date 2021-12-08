package de.dercompiler.optimization.ConstantPropagation;

import firm.TargetValue;
import firm.nodes.*;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class TransferFunctionVisitor implements NodeVisitor {
    private ITransferFunction transferFunction;

    private Queue<Node> nodeQueue;
    private HashMap<Node, List<Node>> successorsMap;
    private HashMap<Node, TargetValue> targetValueMap;

    public TransferFunctionVisitor(ITransferFunction transferFunction, Queue<Node> nodeQueue, HashMap<Node, List<Node>> successorsMap, HashMap<Node, TargetValue> targetValueMap){
        this.transferFunction = transferFunction;
        this.nodeQueue = nodeQueue;
        this.successorsMap = successorsMap;
        this.targetValueMap = targetValueMap;
    }

    /**
     * Compares two {@link TargetValue}s
     * @param t1 The first TargetValue
     * @param t2 The second TargetValue
     * @return Whether both values are equal
     */
    private boolean areEqual(TargetValue t1, TargetValue t2) {
        if (t1.equals(TargetValue.getUnknown())) {
            return t2.equals(TargetValue.getUnknown());
        }
        if (t1.equals(TargetValue.getBad())) {
            return t2.equals(TargetValue.getBad());
        }
        return t1.asInt() == t2.asInt();
    }

    private void doAlways(Node node, TargetValue newValue){
        TargetValue oldValue = targetValueMap.get(node);
        
        if (!areEqual(oldValue, newValue)){
            targetValueMap.put(node, newValue);
            
            nodeQueue.addAll(successorsMap.get(node));
        }
    }

    @Override
    public void visit(Add node) {doAlways(node, transferFunction.getTargetValue(node));};

    @Override
    public void visit(Address node) {}

    @Override
    public void visit(Align node) {}

    @Override
    public void visit(Alloc node) {}

    @Override
    public void visit(Anchor node) {}

    @Override
    public void visit(And node) {doAlways(node, transferFunction.getTargetValue(node));};

    @Override
    public void visit(Bad node) {}

    @Override
    public void visit(Bitcast node) {}

    @Override
    public void visit(Block node) {}

    @Override
    public void visit(Builtin node) {}

    @Override
    public void visit(Call node) {}

    @Override
    public void visit(Cmp node) {doAlways(node, transferFunction.getTargetValue(node));};

    @Override
    public void visit(Cond node) {}

    @Override
    public void visit(Confirm node) {}

    @Override
    public void visit(Const node) {doAlways(node, transferFunction.getTargetValue(node));};

    @Override
    public void visit(Conv node) {}

    @Override
    public void visit(CopyB node) {}

    @Override
    public void visit(Deleted node) {}

    @Override
    public void visit(Div node) {doAlways(node, transferFunction.getTargetValue(node));};

    @Override
    public void visit(Dummy node) {}

    @Override
    public void visit(End node) {}

    @Override
    public void visit(Eor node) {doAlways(node, transferFunction.getTargetValue(node));};

    @Override
    public void visit(Free node) {}

    @Override
    public void visit(IJmp node) {}

    @Override
    public void visit(Id node) {doAlways(node, transferFunction.getTargetValue(node));}

    @Override
    public void visit(Jmp node) {}

    @Override
    public void visit(Load node) {}

    @Override
    public void visit(Member node) {}

    @Override
    public void visit(Minus node) {doAlways(node, transferFunction.getTargetValue(node));}

    @Override
    public void visit(Mod node) {doAlways(node, transferFunction.getTargetValue(node));}

    @Override
    public void visit(Mul node) {doAlways(node, transferFunction.getTargetValue(node));}

    @Override
    public void visit(Mulh node) {doAlways(node, transferFunction.getTargetValue(node));}

    @Override
    public void visit(Mux node) {doAlways(node, transferFunction.getTargetValue(node));}

    @Override
    public void visit(NoMem node){}

    @Override
    public void visit(Not node) {doAlways(node, transferFunction.getTargetValue(node));}

    @Override
    public void visit(Offset node) {}

    @Override
    public void visit(Or node) {doAlways(node, transferFunction.getTargetValue(node));}

    @Override
    public void visit(Phi node) {}

    @Override
    public void visit(Pin node) {}

    @Override
    public void visit(Proj node) {}

    @Override
    public void visit(Raise node) {}

    @Override
    public void visit(Return node) {}

    @Override
    public void visit(Sel node) {}

    @Override
    public void visit(Shl node) {doAlways(node, transferFunction.getTargetValue(node));};

    @Override
    public void visit(Shr node) {doAlways(node, transferFunction.getTargetValue(node));};

    @Override
    public void visit(Shrs node) {doAlways(node, transferFunction.getTargetValue(node));};

    @Override
    public void visit(Size node) {}
    @Override

    public void visit(Start node) {}
    @Override

    public void visit(Store node) {}

    @Override
    public void visit(Sub node) {doAlways(node, transferFunction.getTargetValue(node));};

    @Override
    public void visit(Switch node) {}

    @Override
    public void visit(Sync node) {}

    @Override
    public void visit(Tuple node) {}

    @Override
    public void visit(Unknown node) {}

    @Override
    public void visitUnknown(Node node) {}
}
