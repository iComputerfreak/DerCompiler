package de.dercompiler.intermediate.selection;

import firm.nodes.*;

public abstract class LazyNodeWalker implements NodeVisitor {
    public void visit(Add add) {
        visitAny(add);
    }

    public void visit(Address address) {
        visitAny(address);
    }

    public void visit(Align align) {
        visitAny(align);
    }

    public void visit(Alloc alloc) {
        visitAny(alloc);
    }

    public void visit(Anchor anchor) {
        visitAny(anchor);
    }

    protected abstract void visitAny(Node node);

    public void visit(And and) {
        visitAny(and);
    }

    public void visit(Bad bad) {
        visitAny(bad);
    }

    public void visit(Bitcast bitcast) {
        visitAny(bitcast);
    }

    public void visit(Block block) {
        visitAny(block);
    }

    public void visit(Builtin builtin) {
        visitAny(builtin);
    }

    public void visit(Call call) {
        visitAny(call);
    }

    public void visit(Cmp cmp) {
        visitAny(cmp);
    }

    public void visit(Cond cond) {
        visitAny(cond);
    }

    public void visit(Confirm confirm) {
        visitAny(confirm);
    }

    public void visit(Const aConst) {
        visitAny(aConst);
    }

    public void visit(Conv conv) {
        visitAny(conv);
    }

    public void visit(CopyB copyB) {
        visitAny(copyB);
    }

    public void visit(Deleted deleted) {
        visitAny(deleted);
    }

    public void visit(Div div) {
        visitAny(div);
    }

    public void visit(Dummy dummy) {
        visitAny(dummy);
    }

    public void visit(End end) {
        visitAny(end);
    }

    public void visit(Eor eor) {
        visitAny(eor);
    }

    public void visit(Free free) {
        visitAny(free);
    }

    public void visit(IJmp iJmp) {
        visitAny(iJmp);
    }

    public void visit(Id id) {
        visitAny(id);
    }

    public void visit(Jmp jmp) {
        visitAny(jmp);
    }

    public void visit(Load load) {
        visitAny(load);
    }

    public void visit(Member member) {
        visitAny(member);
    }

    public void visit(Minus minus) {
        visitAny(minus);
    }

    public void visit(Mod mod) {
        visitAny(mod);
    }

    public void visit(Mul mul) {
        visitAny(mul);
    }

    public void visit(Mulh mulh) {
        visitAny(mulh);
    }

    public void visit(Mux mux) {
        visitAny(mux);
    }

    public void visit(NoMem noMem) {
        visitAny(noMem);
    }

    public void visit(Not not) {
        visitAny(not);
    }

    public void visit(Offset offset) {
        visitAny(offset);
    }

    public void visit(Or or) {
        visitAny(or);
    }

    public void visit(Phi phi) {
        visitAny(phi);
    }

    public void visit(Pin pin) {
        visitAny(pin);
    }

    public void visit(Proj proj) {
        visitAny(proj);
    }

    public void visit(Raise raise) {
        visitAny(raise);
    }

    public void visit(Return aReturn) {
        visitAny(aReturn);
    }

    public void visit(Sel sel) {
        visitAny(sel);
    }

    public void visit(Shl shl) {
        visitAny(shl);
    }

    public void visit(Shr shr) {
        visitAny(shr);
    }

    public void visit(Shrs shrs) {
        visitAny(shrs);
    }

    public void visit(Size size) {
        visitAny(size);
    }

    public void visit(Start start) {
        visitAny(start);
    }

    public void visit(Store store) {
        visitAny(store);
    }

    public void visit(Sub sub) {
        visitAny(sub);
    }

    public void visit(Switch aSwitch) {
        visitAny(aSwitch);
    }

    public void visit(Sync sync) {
        visitAny(sync);
    }

    public void visit(Tuple tuple) {
        visitAny(tuple);
    }

    public void visit(Unknown unknown) {
        visitAny(unknown);
    }

    public void visitUnknown(Node node) {
        visitAny(node);
    }
}
