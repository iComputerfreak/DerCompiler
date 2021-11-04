package de.dercompiler.ast;

import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.type.Type;
import de.dercompiler.util.Utils;

import java.util.List;

public final class Method extends ClassMember {
    
    private final Type type;
    private final String identifier;
    private final List<Parameter> parameters;
    private final MethodRest rest;
    private final BasicBlock block;
    
    // INFO: methodRest may be null
    public Method(Type type, String identifier, List<Parameter> parameters, MethodRest rest, BasicBlock block) {
        this.type = type;
        this.identifier = identifier;
        this.parameters = parameters;
        this.rest = rest;
        this.block = block;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public MethodRest getRest() {
        return rest;
    }

    public BasicBlock getBlock() {
        return block;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof Method otherMethod) {
            return this.type.syntaxEquals(otherMethod.type)
                    && this.identifier.equals(otherMethod.identifier)
                    && Utils.syntaxEquals(this.parameters, otherMethod.parameters)
                    && this.rest.syntaxEquals(otherMethod.rest)
                    && this.block.syntaxEquals(otherMethod.block);
        }
        return false;
    }
}
