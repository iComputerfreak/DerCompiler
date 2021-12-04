package de.dercompiler.transformation;

import de.dercompiler.ast.statement.IfStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.statement.WhileStatement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import firm.Mode;
import firm.Relation;
import firm.Type;
import firm.nodes.*;

public class TransformationHelper {

    public static Node calculateSize(TransformationState state, Node type_size, Node num_values) {
        return calculateOffset(state, type_size, num_values);
    }

    public static Node calculateOffset(TransformationState state, Node type_size, Node num_values) {
        return state.construction.newMul(type_size, num_values);
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
        createJump(state, to, state.construction.newJmp());
    }

    public static void createJump(TransformationState state, Block to, Node jmp) {
        to.addPred(jmp);
    }

    public static Node createComp(TransformationState state, Relation relation) {
        return state.construction.newCmp(state.lhs, state.rhs, relation);
    }

    public static void createConditionJumps(TransformationState state, Node cmp) {
        Node cond = state.construction.newCond(cmp);
        Node jmpTrue = state.construction.newProj(cond, Mode.getX(), Cond.pnTrue);
        Node jmpFalse = state.construction.newProj(cond, Mode.getX(), Cond.pnFalse);
        TransformationHelper.createJump(state, state.trueBlock(), jmpTrue);
        TransformationHelper.createJump(state, state.falseBlock(), jmpFalse);
    }

    public static boolean isControlStructure(Statement statement) {
        return statement instanceof IfStatement || statement instanceof WhileStatement;
    }

    public static Node createBooleanNode(TransformationState state, boolean value) {
        return state.construction.newConst(value ? 1 : 0, Mode.getBu());
    }

    public static void createReturn(TransformationState state, Node node) {
        Node[] res = new Node[0];
        if (node != null ) {
            res = new Node[]{ node };
        }
        state.graph.getEndBlock().addPred(state.construction.newReturn(state.construction.getCurrentMem(), res));
    }

    public static void createConditionError() {
        new OutputMessageHandler(MessageOrigin.TRANSFORM)
                .internalError("We are in the Mode of constructing a condition, but no true and False Block set!");
    }
}
