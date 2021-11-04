package de.dercompiler.ast.type;


import de.dercompiler.ast.ASTNode;

public abstract sealed class BasicType implements ASTNode permits IntType, BooleanType, VoidType, CustomType {}
