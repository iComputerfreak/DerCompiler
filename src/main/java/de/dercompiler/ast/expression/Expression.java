package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class Expression extends ASTNode permits BinaryExpression, ErrorExpression, PrimaryExpression, UnaryExpression, UninitializedValue, VoidExpression {

    private Statement surrounding = null;
    private Type type;

    protected Expression(SourcePosition position) {
        super(position);
        this.type = null;
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
}
