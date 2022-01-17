package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTDefinition;
import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.Field;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.semantic.type.BooleanType;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.RValueNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Mode;
import firm.Relation;
import firm.Type;
import firm.nodes.Block;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.ASSIGN;

public final class AssignmentExpression extends BinaryExpression {

    public AssignmentExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public OperatorToken getOperator() {
        return ASSIGN;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof AssignmentExpression ae) {
            return syntaxEqualLhsRhs(ae);
        }
        return false;
    }

    private ReferenceNode createBooleanValueNode(TransformationState state) {
        state.pushExpectValue();
        createChildNodes(state);
        state.popExpect();

        state.lhs.genStore(state, state.rhs);
        ReferenceNode node = null;
        if (state.expectValue()) {
            node = state.rhs;
        } else {
            Node cmp = state.construction.newCmp(state.rhs.genLoad(state), TransformationHelper.createBooleanNode(state, true), Relation.Equal);
            TransformationHelper.createConditionJumps(state, cmp);
        }
        clearChildNodes(state);
        return node;
    }

    private void createBooleanBranchNode(TransformationState state) {
        state.pushBranches(state.construction.newBlock(), state.construction.newBlock());
        //state.pushExpectValue();
        state.isAsignement = true;
        createChildNodes(state);
        state.isAsignement = false;
        //state.popExpect();
        afterCreatedBranches(state);
        clearChildNodes(state);
    }

    private void afterCreatedBranches(TransformationState state) {
        Block cur = state.construction.getCurrentBlock();
        Block assignTrue = state.trueBlock();                       //getter information and setup following complicated cases
        Block assignFalse = state.falseBlock();
        assignTrue.mature();
        assignFalse.mature();
        state.popBranches();

        state.construction.setCurrentBlock(assignTrue);                         //assign true in case of true
        Node nodeT = TransformationHelper.createBooleanNode(state, true);
        state.lhs.genStore(state, new RValueNode(nodeT, getType()));         // create jump to original true-block
        TransformationHelper.createDirectJump(state, state.trueBlock());

        state.construction.setCurrentBlock(assignFalse);                        //assign false in case of false
        Node nodeF = TransformationHelper.createBooleanNode(state, false);
        state.lhs.genStore(state, new RValueNode(nodeF, getType()));         // create jump to original false-block
        TransformationHelper.createDirectJump(state, state.falseBlock());

        state.construction.setCurrentBlock(cur);
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        boolean isBooleanAssign = getLhs().getType().isCompatibleTo(new BooleanType());
        ReferenceNode res = null;
        if (isBooleanAssign) {     //in case lhs is boolean we create new branches
            if (state.expectValue()) {
                res = createBooleanValueNode(state);
            } else {
                createBooleanBranchNode(state);
            }
        } else {
            createChildNodes(state);
            state.lhs.genStore(state, state.rhs);
            res = state.rhs;
            clearChildNodes(state);
        }
        return res;
    }
}
