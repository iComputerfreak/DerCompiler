package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.bindings.binding_ircons;
import firm.nodes.Div;
import firm.nodes.Node;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.PERCENT_SIGN;

public final class ModuloExpression extends BinaryExpression {

    public ModuloExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ModuloExpression me) {
            return syntaxEqualLhsRhs(me);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return PERCENT_SIGN;
    }

    @Override
    public Node createNode(TransformationState state) {
        createChildNodes(state);
        Node mem = state.construction.getCurrentMem();
        Node div = state.construction.newMod(mem, state.lhs, state.rhs, binding_ircons.op_pin_state.op_pin_state_pinned);
        clearChildNodes(state);

        Mode mode = state.lhs.getMode();
        state.construction.setCurrentMem(state.construction.newProj(div, mode, Div.pnM));
        return state.construction.newProj(div, mode, Div.pnRes);
    }
}
