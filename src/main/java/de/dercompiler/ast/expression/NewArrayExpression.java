package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.FirmTypes;
import de.dercompiler.transformation.LibraryMethods;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ArrayNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Entity;
import firm.Mode;
import firm.Type;
import firm.nodes.Call;
import firm.nodes.Node;

import java.util.Objects;

public final class NewArrayExpression extends PrimaryExpression {

    private final BasicType type;
    private final Expression size;
    private final int dimension;

    public NewArrayExpression(SourcePosition position, BasicType type, Expression size, int dimension) {
        super(position);
        this.type = type;
        this.size = size;
        this.dimension = dimension;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NewArrayExpression nae) {
            return type.syntaxEquals(nae.type)
                    && dimension == nae.dimension
                    && size.syntaxEquals(nae.size);
        }
        return false;
    }

    public BasicType getBasicType() {
        return type;
    }

    public Expression getNumElements() {
        return size;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitNewArrayExpression(this);
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        Node mem = state.construction.getCurrentMem();
        Type type = getType().getFirmTransformationType();

        Node type_size = state.construction.newConst(type.getSize(), FirmTypes.longFirmType.getMode());
        Entity methodEntity = LibraryMethods.allocate;
        Node call = state.construction.newCall(mem,
                state.construction.newAddress(methodEntity),new Node[]{ TransformationHelper.intToOffset(state, getNumElements().createNode(state).genLoad(state)), type_size }, methodEntity.getType());

        state.construction.setCurrentMem(state.construction.newProj(call, Mode.getM(), Call.pnM));
        Node tuple = state.construction.newProj(call, Mode.getT(), Call.pnTResult);
        return new ArrayNode(state.construction.newProj(tuple, Mode.getP(), 0), getType(), dimension);
    }
}
