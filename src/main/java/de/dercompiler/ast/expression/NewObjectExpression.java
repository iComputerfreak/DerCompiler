package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.transformation.FirmTypes;
import de.dercompiler.transformation.LibraryMethods;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ObjectNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Entity;
import firm.Mode;
import firm.nodes.Call;
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
    public ReferenceNode createNode(TransformationState state) {
        Node mem = state.construction.getCurrentMem();

        ClassType type = state.globalScope.getClass(getObjectType().getIdentifier());
        int size = type.getFirmType().getSize();

        Node type_size = state.construction.newConst(size, FirmTypes.offsetType.getMode());
        Entity methodEntity = LibraryMethods.allocate;
        Node call = state.construction.newCall(mem,
                state.construction.newAddress(methodEntity),new Node[]{ type_size }, methodEntity.getType());

        state.construction.setCurrentMem(state.construction.newProj(call, Mode.getM(), Call.pnM));
        Node tuple = state.construction.newProj(call, Mode.getT(), Call.pnTResult);
        return new ObjectNode(state.construction.newProj(tuple, Mode.getP(), 0), type);
    }
}
