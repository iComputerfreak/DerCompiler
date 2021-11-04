package de.dercompiler.ast;

public abstract sealed class ClassMember implements ASTNode permits Field, Method, MainMethod {}
