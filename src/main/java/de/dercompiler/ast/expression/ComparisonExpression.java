package de.dercompiler.ast.expression;

import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Relation;
import firm.nodes.Node;

public sealed abstract class ComparisonExpression extends BinaryExpression permits EqualExpression, GreaterEqualExpression, GreaterExpression, LessEqualExpression, LessExpression, NotEqualExpression {

    public ComparisonExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    public Node createComp(TransformationState state, Relation relation) {
        state.pushExpectValue();

        ReferenceNode lhs = createLhs(state);
        Node lhs_raw;
        if (lhs.isReference()) {
            lhs_raw = lhs.getReference();
        } else { //null incuded
            lhs_raw = lhs.genLoad(state);
        }

        ReferenceNode rhs = createRhs(state);
        Node rhs_raw;
        if (rhs.isReference()) {
            rhs_raw = rhs.getReference();
        } else {
            rhs_raw = rhs.genLoad(state);
        }

        state.popExpect();

        return state.construction.newCmp(lhs_raw, rhs_raw, relation);
    }
}
