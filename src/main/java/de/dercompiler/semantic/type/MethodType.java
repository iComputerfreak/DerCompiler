package de.dercompiler.semantic.type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodType implements Type {

    private final boolean staticMethod;
    private final List<Type> parameterTypes;
    private final Type returnType;

    public MethodType(Type type, List<Type> parameters, boolean isStatic) {
        this.returnType = type;
        this.parameterTypes = parameters;
        this.staticMethod = isStatic;
    }

    public MethodType(Type returnType, Type... parameterTypes) {
        this(returnType, Arrays.asList(parameterTypes), false);
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

    @Override
    public String toString() {
        String params = parameterTypes.isEmpty() ? "void" : parameterTypes.stream().map(t -> "\\" + t.toString() + " ").collect(Collectors.joining());
        return "%s -> %s".formatted(params, returnType);
    }

    public boolean isStaticMethod() {
        return staticMethod;
    }
}
