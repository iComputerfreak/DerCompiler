package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.RValueNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Mode;
import firm.bindings.binding_ircons;
import firm.nodes.Div;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.SLASH;

public final class DivisionExpression extends BinaryExpression {

    public DivisionExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof DivisionExpression de) {
            return syntaxEqualLhsRhs(de);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return SLASH;
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        createChildNodes(state);
        Node mem = state.construction.getCurrentMem();
        Node div = state.construction.newDiv(mem, state.lhs.genLoad(state), state.rhs.genLoad(state), binding_ircons.op_pin_state.op_pin_state_pinned);
        Mode resMode = TransformationHelper.unifyMode(state.lhs.getMode(), state.rhs.getMode());
        state.construction.setCurrentMem(state.construction.newProj(div, Mode.getM(), Div.pnM));
        clearChildNodes(state);
        return new RValueNode(state.construction.newProj(div, resMode, Div.pnRes), getType());
    }
}
