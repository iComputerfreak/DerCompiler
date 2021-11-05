package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class Statement extends ASTNode permits BasicBlock, EmptyStatement, ErrorStatement, ExpressionStatement, IfStatement, LocalVariableDeclarationStatement, ReturnStatement, WhileStatement {
    
    public Statement(SourcePosition position) {
        super(position);
    }

}
