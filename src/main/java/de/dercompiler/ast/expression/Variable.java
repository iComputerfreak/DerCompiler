package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTDefinition;
import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.Field;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.type.BooleanType;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.FieldNode;
import de.dercompiler.transformation.node.LocalVariableNode;
import de.dercompiler.transformation.node.RValueNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Entity;
import firm.Mode;
import firm.Relation;
import firm.nodes.Node;

import java.util.Objects;

public final class Variable extends PrimaryExpression {

    private final String name;
    // True if the type of this Variable is an instance of InternalClass.

    public ASTDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(ASTDefinition definition) {
        this.definition = definition;
    }

    private ASTDefinition definition;

    public Variable(SourcePosition position, String name) {
        super(position);
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof Variable v) {
            return name.equals(v.name);
        }
        return false;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitVariable(this);
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        ASTDefinition def = getDefinition();
        ReferenceNode res;
        if (def instanceof LocalVariableDeclarationStatement lvds) {
            int nodeId = lvds.getNodeId();
            res = new LocalVariableNode(lvds.getRefType(), nodeId);
        } else if (def instanceof Parameter p) {
            res = new LocalVariableNode(getType(), p.getNodeId());
        } else if (def instanceof Field f) {
            Node this_ = state.construction.newProj(state.graph.getArgs(), Mode.getP(), 0);
            Entity field = state.globalScope.getMemberEntity(f.getClassDeclaration().getIdentifier(), f.getMangledIdentifier());
            res = new FieldNode(state.construction.newMember(this_, field), f.getRefType());
        } else {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("Variable can only have a Field, Parameter or LocalVariableDefinition, but we got: " + def.getClass().getName());
            return null; //we never return
        }
        if (def.getRefType().isCompatibleTo(new BooleanType()) && !state.expectValue()) {
            TransformationHelper.booleanValueToConditionalJmp(state, res.genLoad(state));
            return null;
        }
        return res;
    }

    @Override
    public String toString() {
        return name;
    }

}
