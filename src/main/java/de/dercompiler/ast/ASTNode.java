package de.dercompiler.ast;

import de.dercompiler.ast.expression.AbstractExpression;

public abstract sealed class ASTNode
        permits Statement, Block, BlockStatement,
                LocalVariableDeclarationStatement, EmptyStatement, WhileStatement, IfStatement, ExpressionStatement,
                ReturnStatement, Arguments {
}
