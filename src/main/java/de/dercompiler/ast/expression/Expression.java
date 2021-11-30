package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Node;

public abstract sealed class Expression extends ASTNode permits BinaryExpression, ErrorExpression, PrimaryExpression, UnaryExpression, UninitializedValue {

    private Statement surrounding = null;
    private Type type;
    // True if this expression is directly surrounded by parentheses.
    private boolean inParentheses;
    // True if the type of this expression is an instance of InternalClass.
    private boolean internal;

    protected Expression(SourcePosition position) {
        super(position);
    }

    public void setSurroundingStatement(Statement statement) {
        surrounding = statement;
    }

    public Statement getSurroundingStatement() {
        return surrounding;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public abstract void accept(ASTExpressionVisitor astExpressionVisitor);

    public abstract Node createNode(TransformationState state);

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        this.accept((ASTExpressionVisitor) astNodeVisitor);
    }

    public void setInParentheses(boolean inParentheses) {
        this.inParentheses = inParentheses;
    }

    public boolean isInParentheses() {
        return inParentheses;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public boolean isInternal() {
        return internal;
    }
}
