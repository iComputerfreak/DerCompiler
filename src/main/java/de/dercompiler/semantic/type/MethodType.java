package de.dercompiler.semantic.type;

import de.dercompiler.ast.Parameter;

import java.util.List;

public class MethodType implements Type {

    private List<Type> parameterTypes;
    private Type returnType;

    public MethodType(Type type, List<Type> parameters) {
        this.returnType = type;
        this.parameterTypes = parameters;
    }

    @Override
    public boolean isCompatibleTo(Type other) {
        return false;
    }

    public List<Type> getParameterTypes() {
        return parameterTypes;
    }

    public Type getReturnType() {
        return returnType;
    }
}
