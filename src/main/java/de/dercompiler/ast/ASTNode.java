package de.dercompiler.ast;

import de.dercompiler.ast.statement.Statement;

public abstract sealed class ASTNode
        permits Program, ClassDeclaration, ClassMember, Field, MainMethod, Method, MethodRest,
                Parameters, ParametersRest, Parameter, Type, TypeRest, BasicType, Statement, Block, BlockStatement,
                LocalVariableDeclarationStatement, EmptyStatement, WhileStatement, IfStatement, ExpressionStatement,
                ReturnStatement, PostfixOp, MethodInvocation, FieldAccess, ArrayAccess, Arguments {
}
