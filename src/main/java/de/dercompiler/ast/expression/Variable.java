package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTDefinition;
import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import firm.Mode;
import firm.nodes.Node;

import java.util.Objects;

public final class Variable extends PrimaryExpression {

    private final String name;
    // True if the type of this Variable is an instance of InternalClass.
    private boolean internal;

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
    public Node createNode(TransformationState state) {
        ASTDefinition def = getDefinition();
        int local_var_id;
        if (def instanceof LocalVariableDeclarationStatement lvds) {
            local_var_id = lvds.getNodeId();
        } else if (def instanceof Parameter p) {
            local_var_id = p.getNodeId();
        } else {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("Variable can only have a Parameter or LocalVariableDefinition, but we got: " + def.getClass().getName());
            return null; //we never return
        }
        //TODO how to get mode?
        Mode mode = null; //-> from type?
        return state.construction.getVariable(local_var_id, mode);
    }

    @Override
    public String toString() {
        return name;
    }

}
