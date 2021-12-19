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

    @Override
    public ReferenceNode createNode(TransformationState state) {
        boolean isBooleanAssign = getLhs().getType().isCompatibleTo(new BooleanType());
        if (isBooleanAssign) {     //in case lhs is boolean we create new branches
            state.pushBranches(state.construction.newBlock(), state.construction.newBlock());
        }
        createChildNodes(state);                                        //create jumps or value
        if (isBooleanAssign) {                                      //in case of boolean
            Block cur = state.construction.getCurrentBlock();

            Block assignTrue = state.trueBlock();                       //getter information and setup following complicated cases
            Block assignFalse = state.falseBlock();
            assignTrue.mature();
            assignFalse.mature();
            state.popBranches();

            state.construction.setCurrentBlock(assignTrue);             //assign true in case of true
            Node nodeT = TransformationHelper.createBooleanNode(state, true);
            state.lhs.genStore(state, new RValueNode(nodeT, Mode.getBu()));
            if (state.isCondition()) {                                  // in case of condition we create jump to the original true-block
                TransformationHelper.createDirectJump(state, state.trueBlock());
            } else {                                                    // in case of assignment expression in single statement
                cur.mature(); //not sure if necessary?
                cur = state.construction.newBlock();                    //override cur, because we need to start our calculations after this in a new block
                TransformationHelper.createDirectJump(state, cur);      //create jump to block after assignment
            }
            state.construction.setCurrentBlock(assignFalse);            //assign false in case of false
            Node nodeF = TransformationHelper.createBooleanNode(state, false);
            state.lhs.genStore(state, new RValueNode(nodeF, Mode.getBu()));
            if (state.isCondition()) {                                  // in case of condition create jump to the original false-block
                TransformationHelper.createDirectJump(state, state.falseBlock());
            } else {                                                    // in case of assignment expression in single statement
                TransformationHelper.createDirectJump(state, cur);
                cur.mature();                                           //mature cur all prev jumps set
            }
            state.construction.setCurrentBlock(cur);
        } else {                                                        //if non boolean value create normal assignment
            state.lhs.genStore(state, state.rhs);
        }                                                               // generate load, so we get the set value again, only needed for non-boolean values
        ReferenceNode res = new RValueNode(state.lhs.genLoad(state), state.lhs.getMode());
        clearChildNodes(state);
        return res;
    }
}
