package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;

public sealed class Statement implements ASTNode permits BasicBlock, EmptyStatement, IfStatement, ExpressionStatement, WhileStatement, ReturnStatement, LocalVariableDeclarationStatement  {

}
