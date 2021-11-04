package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;

public abstract sealed class Statement implements ASTNode permits BasicBlock, EmptyStatement, ErrorStatement, ExpressionStatement, IfStatement, LocalVariableDeclarationStatement, ReturnStatement, WhileStatement {

}
