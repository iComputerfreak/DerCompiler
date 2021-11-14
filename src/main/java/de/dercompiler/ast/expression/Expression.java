package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class Expression extends ASTNode permits BinaryExpression, ErrorExpression, PrimaryExpression, UnaryExpression, UninitializedValue, VoidExpression {

    private Statement surrounding = null;

    protected Expression(SourcePosition position) { super(position); }

    public void setSurroundingStatement(Statement statement) {
        surrounding = statement;
    }

    public Statement getSurroundingStatement() {
        return surrounding;
    }

}
