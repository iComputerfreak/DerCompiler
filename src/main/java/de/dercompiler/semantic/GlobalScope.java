package de.dercompiler.semantic;

import de.dercompiler.semantic.type.*;
import de.dercompiler.transformation.FirmTypeFactory;
import de.dercompiler.ast.ClassMember;
import de.dercompiler.ast.Field;
import de.dercompiler.ast.Method;
import de.dercompiler.semantic.type.ClassType;

import java.util.*;

public class GlobalScope {
    
    private final Map<String, ClassType> classMap;

    public GlobalScope() {
        this.classMap = new HashMap<>();
    }

    public MethodDefinition getMethod(String className, String methodName) {
        return getClass(className).getMethod(methodName);
    }

    public FieldDefinition getField(String className, String fieldName) {
        return getClass(className).getField(fieldName);
    }

    public ClassType getClass(String className) {
        return classMap.get(className);
    }

    public boolean hasClass(String identifier) {
        return classMap.containsKey(identifier);
    }

    public void addClass(ClassType newClass) {
        classMap.put(newClass.getIdentifier(), newClass);
    }

    public List<ClassType> getClasses() {
        return new ArrayList<>(this.classMap.values());
    }
}
