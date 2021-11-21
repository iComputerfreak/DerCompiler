package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class Expression extends ASTNode permits BinaryExpression, ErrorExpression, PrimaryExpression, UnaryExpression, UninitializedValue, VoidExpression {

    private Statement surrounding = null;
    private Type type;
    private boolean inParentheses;

    protected Expression(SourcePosition position) {
        super(position);
    }

    public void setSurroundingStatement(Statement statement) {
        surrounding = statement;
    }

    public Statement getSurroundingStatement() {
        return surrounding;
    }


    //getType(HashMap<String, StringTable> fieldStringtables, HashMap<String, StringTable> methodStringTables
    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public abstract void accept(ASTExpressionVisitor astExpressionVisitor);

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
}
