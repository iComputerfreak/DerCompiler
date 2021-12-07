package de.dercompiler.transformation;

import firm.nodes.*;

import java.util.HashMap;
import java.util.Queue;

public abstract class TransferFunction<L extends Lattice> implements NodeVisitor {
    public abstract L getTargetValue(Add node);
    public abstract L getTargetValue(Address node);
    public abstract L getTargetValue(Align node);
    public abstract L getTargetValue(Alloc node);
    public abstract L getTargetValue(Anchor node);
    public abstract L getTargetValue(And node);
    public abstract L getTargetValue(Bad node);
    public abstract L getTargetValue(Bitcast node);
    public abstract L getTargetValue(Block node);
    public abstract L getTargetValue(Builtin node);
    public abstract L getTargetValue(Call node);
    public abstract L getTargetValue(Cmp node);
    public abstract L getTargetValue(Cond node);
    public abstract L getTargetValue(Confirm node);
    public abstract L getTargetValue(Const node);
    public abstract L getTargetValue(Conv node);
    public abstract L getTargetValue(CopyB node);
    public abstract L getTargetValue(Deleted node);
    public abstract L getTargetValue(Div node);
    public abstract L getTargetValue(Dummy node);
    public abstract L getTargetValue(End node);
    public abstract L getTargetValue(Eor node);
    public abstract L getTargetValue(Free node);
    public abstract L getTargetValue(IJmp node);
    public abstract L getTargetValue(Id node);
    public abstract L getTargetValue(Jmp node);
    public abstract L getTargetValue(Load node);
    public abstract L getTargetValue(Member node);
    public abstract L getTargetValue(Minus node);
    public abstract L getTargetValue(Mod node);
    public abstract L getTargetValue(Mul node);
    public abstract L getTargetValue(Mulh node);
    public abstract L getTargetValue(Mux node);
    public abstract L getTargetValue(NoMem node);
    public abstract L getTargetValue(Not node);
    public abstract L getTargetValue(Offset node);
    public abstract L getTargetValue(Or node);
    public abstract L getTargetValue(Phi node);
    public abstract L getTargetValue(Pin node);
    public abstract L getTargetValue(Proj node);
    public abstract L getTargetValue(Raise node);
    public abstract L getTargetValue(Return node);
    public abstract L getTargetValue(Sel node);
    public abstract L getTargetValue(Shl node);
    public abstract L getTargetValue(Shr node);
    public abstract L getTargetValue(Shrs node);
    public abstract L getTargetValue(Size node);
    public abstract L getTargetValue(Start node);
    public abstract L getTargetValue(Store node);
    public abstract L getTargetValue(Sub node);
    public abstract L getTargetValue(Switch node);
    public abstract L getTargetValue(Sync node);
    public abstract L getTargetValue(Tuple node);
    public abstract L getTargetValue(Unknown node);

    public HashMap<Node, L> targetValues;
    public Queue<Node> nodeQueue;

    public TransferFunction(HashMap<Node,L> targetValues, Queue<Node> nodeQueue){
        this.targetValues = targetValues;
        this.nodeQueue = nodeQueue;
    }

    public void doAlways(Node visitedNode, L newValue){
        if (!targetValues.get(visitedNode).equals(newValue)){
            targetValues.put(visitedNode, newValue);
            nodeQueue.add(visitedNode);
        }
    }

    @Override
    public void visit(Add node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Address node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Align node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Alloc node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Anchor node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(And node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Bad node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Bitcast node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Block node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Builtin node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Call node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Cmp node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Cond node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Confirm node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Const node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Conv node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(CopyB node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Deleted node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Div node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Dummy node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(End node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Eor node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Free node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(IJmp node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Id node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Jmp node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Load node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Member node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Minus node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Mod node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Mul node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Mulh node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Mux node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(NoMem node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Not node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Offset node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Or node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Phi node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Pin node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Proj node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Raise node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Return node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Sel node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Shl node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Shr node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Shrs node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Size node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Start node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Store node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Sub node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Switch node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Sync node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Tuple node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visit(Unknown node) {
        doAlways(node, getTargetValue(node));
    }

    @Override
    public void visitUnknown(Node node) {

    }
}
