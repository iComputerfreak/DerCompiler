package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
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
        Node res;
        if (getLhs() instanceof Variable v) {
            if (v.getDefinition() instanceof LocalVariableDeclarationStatement lvds) {
                state.construction.setVariable(lvds.getNodeId(), state.rhs);
                res = state.rhs;
            } else if (v.getDefinition() instanceof Parameter p) {
                state.construction.setVariable(p.getNodeId(), state.rhs);
                res = state.rhs;
            } else {
                new OutputMessageHandler(MessageOrigin.PASSES).internalError("cannot assign Value to variable, because Definition is no local accessible function: " + v.getDefinition());
                return null; //we never return
            }
        } else {
            //assume we need to store
            //TODO: how to get ResType?
            Type type = null;
            TransformationHelper.genStore(state, state.lhs, state.rhs, type);
            res = state.rhs;
        }
        clearChildNodes(state);
        return res;
    }
}
