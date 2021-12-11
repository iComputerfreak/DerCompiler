package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.type.BooleanType;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.IntType;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.type.IntegerType;
import de.dercompiler.transformation.LibraryMethods;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
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

    public Expression getSize() {
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
    public Node createNode(TransformationState state) {
        Node mem = state.construction.getCurrentMem();
        Type type;
        if (getBasicType() instanceof IntType) {
            type = Mode.getIs().getType();
        } else if (getBasicType() instanceof BooleanType) {
            type = Mode.getBs().getType();
        } else if (getBasicType() instanceof CustomType) {
            type = Mode.getP().getType();
        } else {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("can't assign type to Array");
            return null; //we nerver return
        }

        Node type_size = state.construction.newConst(type.getSize(), Mode.getIu());
        //TODO getAlignment in bit or byte? 8byte because of 64bit?
        Node size = TransformationHelper.calculateSize(state, type_size, getSize().createNode(state));
        Entity methodEntity = LibraryMethods.allocate;
        Node call = state.construction.newCall(mem,
                state.construction.newAddress(methodEntity),new Node[]{ size }, methodEntity.getType());

        state.construction.setCurrentMem(state.construction.newProj(call, Mode.getM(), Call.pnM));
        Node tuple = state.construction.newProj(call, Mode.getT(), Call.pnTResult);
        return state.construction.newProj(tuple, Mode.getP(), 0);
    }
}
