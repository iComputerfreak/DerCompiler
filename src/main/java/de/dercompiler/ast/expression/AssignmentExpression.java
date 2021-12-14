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
import de.dercompiler.transformation.node.ReferenceNode;
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
    public ReferenceNode createNode(TransformationState state) {
        createChildNodes(state);
        state.lhs.genStore(state, state.rhs);
        ReferenceNode res = state.rhs;
        clearChildNodes(state);
        return res;
    }
}
