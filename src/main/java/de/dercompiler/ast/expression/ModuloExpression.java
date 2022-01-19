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
    public ReferenceNode createNode(TransformationState state) {
        Node mem = state.construction.getCurrentMem();

        ReferenceNode lhs = createLhs(state);
        Node lhs_raw = lhs.genLoad(state);

        ReferenceNode rhs = createRhs(state);
        Node rhs_raw = rhs.genLoad(state);

        Node div = state.construction.newMod(mem, lhs_raw, rhs_raw, binding_ircons.op_pin_state.op_pin_state_pinned);

        Mode mode = TransformationHelper.unifyMode(lhs.getMode(), rhs.getMode());
        state.construction.setCurrentMem(state.construction.newProj(div, Mode.getM(), Div.pnM));
        return new RValueNode(state.construction.newProj(div, mode, Div.pnRes), getType());
    }
}
