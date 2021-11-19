package de.dercompiler.semantic.type;

import de.dercompiler.ast.Field;
import de.dercompiler.ast.Method;

import java.util.Map;

public class CustomType implements Type {

    private String identifier;
    private Map<String, Field> fieldMap;
    private Map<String, Method> methodMap;

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isCompatibleTo(Type other) {
        return this == other;
    }
}
