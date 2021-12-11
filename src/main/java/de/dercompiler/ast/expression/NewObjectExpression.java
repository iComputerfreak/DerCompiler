package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

import java.util.Objects;

public final class NewObjectExpression extends PrimaryExpression {

    private final CustomType objType;
    public NewObjectExpression(SourcePosition position, CustomType type) {
        super(position);
        this.objType = type;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NewObjectExpression noe) {
            return objType.syntaxEquals(noe.objType);
        }
        return false;
    }

    public CustomType getObjectType() {
        return objType;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitNewObjectExpression(this);
    }

    @Override
    public Node createNode(TransformationState state) {
        Node mem = state.construction.getCurrentMem();

        //TODO getTypeSize();
        Node type_size = state.construction.newConst(1, Mode.getIu());

        //TODO getAlignment in bit or byte? 8byte because of 64bit?
        int align = 0;
        return state.construction.newAlloc(mem, type_size, align);
    }
}
