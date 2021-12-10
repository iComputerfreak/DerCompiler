package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.Relation;
import firm.nodes.Block;
import firm.nodes.Node;

import java.util.Objects;

public final class BooleanValue extends PrimaryExpression {

    private final boolean value;

    public BooleanValue(SourcePosition position, boolean value) {
        super(position);
        this.value = value;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof BooleanValue bv) {
            return value == bv.value;
        }
        return false;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitBooleanValue(this);
    }

    @Override
    public Node createNode(TransformationState state) {
        if (state.isCondition()) {
            Relation relation = Relation.Equal;
            if (!value) {
                relation = relation.negated();
            }
            Node dummy = state.construction.newConst(0, Mode.getBs());
            TransformationHelper.createConditionJumps(state, state.construction.newCmp(dummy, dummy, relation));
        }
        return null;
    }


}
