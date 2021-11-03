package de.dercompiler.ast;

import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.type.Type;

import java.util.LinkedList;

public final class Method extends ClassMember {
    // INFO: parameters and methodRest may be null
    public Method(Type type, String identifier, LinkedList<Parameter> parameters, MethodRest rest, BasicBlock block) {
        
    }
}
