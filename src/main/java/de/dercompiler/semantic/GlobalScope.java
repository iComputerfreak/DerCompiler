package de.dercompiler.semantic;

import de.dercompiler.semantic.type.ClassType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Returns the {@link firm.Entity} for the method or field in the given class with the given name
     * @param className The unmangled class name
     * @param memberName The mangled name of the member
     * @return The requested {@link firm.Entity}
     */
    public firm.Entity getMemberEntity(String className, String memberName) {
        ClassType c = classMap.get(className);
        return c.getFirmType().getMemberByName(memberName);
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
