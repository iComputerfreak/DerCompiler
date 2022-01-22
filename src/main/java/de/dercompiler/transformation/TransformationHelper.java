package de.dercompiler.transformation;

import de.dercompiler.ast.expression.ComparisonExpression;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.node.RValueNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Mode;
import firm.Relation;
import firm.Type;
import firm.nodes.*;

public class TransformationHelper {

    public static Node intToOffset(TransformationState state, Node value) {
        return state.construction.newConv(value, FirmTypes.longFirmType.getMode());
    }

    public static Node calculateOffset(TransformationState state, Node type_size, Node num_values) {
        return state.construction.newMul(intToOffset(state, type_size), intToOffset(state, num_values));
    }

    public static Node addOffsetToPointer(TransformationState state, Node pointer, Node offset) {
        return state.construction.newAdd(pointer, offset);
    }

    public static Node genLoad(TransformationState state, Node pointer, Mode mode) {
        Node mem = state.construction.getCurrentMem();
        Node load = state.construction.newLoad(mem, pointer, mode);
        Node loadRes = state.construction.newProj(load, mode, Load.pnRes);
        state.construction.setCurrentMem(state.construction.newProj(load, Mode.getM(), Load.pnM));
        return loadRes;
    }

    public static void genStore(TransformationState state, Node pointer, Node value, Type type) {
        Node mem = state.construction.getCurrentMem();
        Node store = state.construction.newStore(mem, pointer, value, type);
        state.construction.setCurrentMem(state.construction.newProj(store, Mode.getM(), Store.pnM));
    }

    public static void createDirectJump(TransformationState state, Block to) {
        createJump(to, state.construction.newJmp());
    }

    public static void createJump(Block to, Node jmp) {
        to.addPred(jmp);
    }

    public static void createConditionJumps(TransformationState state, Node cmp) {
        Node cond = state.construction.newCond(cmp);
        Node jmpTrue = state.construction.newProj(cond, Mode.getX(), Cond.pnTrue);
        Node jmpFalse = state.construction.newProj(cond, Mode.getX(), Cond.pnFalse);
        TransformationHelper.createJump(state.trueBlock(), jmpTrue);
        TransformationHelper.createJump(state.falseBlock(), jmpFalse);
    }

    public static Node createBooleanNode(TransformationState state, boolean value) {
        return state.construction.newConst(value ? 1 : 0, FirmTypes.booleanFirmType.getMode());
    }

    public static void createReturn(TransformationState state, Node node) {
        Node[] preds = (node == null) ? new Node[0] : new Node[]{ node };
        state.graph.getEndBlock().addPred(state.construction.newReturn(state.construction.getCurrentMem(), preds));
    }

    public static void createConditionError() {
        new OutputMessageHandler(MessageOrigin.TRANSFORM)
                .internalError("We are in the Mode of constructing a condition, but no true and False Block set!");
    }

    public static Mode unifyMode(Mode lhs, Mode rhs) {
        boolean signedUnsigned = lhs.isSigned() ^ rhs.isSigned();
        boolean oneInt = lhs.isInt() ^ rhs.isInt();
        //reference == pointer in libFIrm
        boolean ptr = lhs.isReference() || rhs.isReference();

        //when pointer we return a pointer
        if (ptr) return Mode.getP();

        if (!lhs.isNum() || !rhs.isNum()) new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("cannot unify Modes lhs: " + lhs.getName() + " rhs: " + rhs.getName());

        if (oneInt) new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("only integer Modes should be unified - lhs: " + lhs + " rhs: " + rhs);
        if (signedUnsigned) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).debugPrint("Check Modes of operations, maybe wrong transformation; lhs: " + lhs.getName() + " rhs: " + rhs.getName());
        }
        if (lhs.isSmallerThan(rhs)) {
            return rhs;
        } else {
            return lhs;
        }
    }

    public static ReferenceNode createComparator(TransformationState state, ComparisonExpression comp, Relation relation, de.dercompiler.semantic.type.Type resType) {
        //we assume here state.lhs and state.rhs are set
        ReferenceNode res = null;
        if (state.expectValue()) {
            Block after = state.construction.newBlock();
            state.pushBranches(after, after);
            createConditionJumps(state, comp.createComp(state, relation));
            state.construction.setCurrentBlock(after);
            state.popBranches();
            res = new RValueNode(state.construction.newPhi( new Node[]{createBooleanNode(state, true), createBooleanNode(state, false)} , Mode.getBu()), resType);
        } else {
            createConditionJumps(state, comp.createComp(state, relation));
        }

        return res;
    }

    public static ReferenceNode createBooleanNot(TransformationState state, ReferenceNode node) {
        if (!node.getMode().equals(Mode.getBu())) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("We only can negate Booleans, but got Mode: " + node.getMode());
        }
        return new RValueNode(state.construction.newEor(node.genLoad(state), createBooleanNode(state, true)), node.getType());
    }

    public static void booleanValueToConditionalJmp(TransformationState state, Node node) {
        if (!node.getMode().equals(Mode.getBu())) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("can't convert non-boolean value to bool");
        }
        Node cmp = state.construction.newCmp(node, createBooleanNode(state, true), Relation.Equal);
        createConditionJumps(state, cmp);
    }
}
