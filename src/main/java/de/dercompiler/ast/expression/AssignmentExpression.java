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
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.Type;
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
    public Node createNode(TransformationState state) {
        createChildNodes(state);
        //TODO if boolean handle assignement properly, similar to LocalVariableDeclaration
        Node res = null;
        if (getLhs() instanceof Variable v) {
            if (v.getDefinition() instanceof LocalVariableDeclarationStatement lvds) {
                if (state.lhs.getMode() == Mode.getP()) {
                    TransformationHelper.genStore(state, state.lhs, state.rhs, lvds.getFirmType());
                } else {
                    state.construction.setVariable(lvds.getNodeId(), state.rhs);
                }
                res = state.rhs;
            // error
            //} else if (v.getDefinition() instanceof Parameter p) {
            } else if (v.getDefinition() instanceof Field f) {
                TransformationHelper.genStore(state, state.lhs, state.rhs, f.getFirmType());
                res = state.rhs;
            } else {
                new OutputMessageHandler(MessageOrigin.PASSES).internalError("cannot assign Value to variable, because Definition is not local accessible: " + v.getDefinition());
                return null; //we never return
            }
        } else {
            new OutputMessageHandler(MessageOrigin.PASSES).internalError("lvalue is no variable, we can't assign anything: " + getLhs());
            return null; //we never return
        }
        clearChildNodes(state);
        return res;
    }
}
