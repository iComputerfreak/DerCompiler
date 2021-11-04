package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class Statement implements ASTNode permits BasicBlock, EmptyStatement, ErrorStatement, ExpressionStatement, IfStatement, LocalVariableDeclarationStatement, ReturnStatement, WhileStatement {

    protected SourcePosition position;

    public Statement(SourcePosition position) {
        this.position = position;
    }

    @Override
    public SourcePosition getSourcePosition() {
        return position;
    }

}
