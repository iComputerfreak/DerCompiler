package de.dercompiler.ast;

import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.util.Utils;

import java.util.List;

public final class Method extends ClassMember {

    private final Type type;
    private final String identifier;
    private final List<Parameter> parameters;
    private final MethodRest rest;
    private final BasicBlock block;
    
    // INFO: methodRest may be null
    public Method(SourcePosition position, Type type, String identifier, List<Parameter> parameters, MethodRest rest, BasicBlock block) {
        super(position);
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
            // If this rest is null, but the other is not, return false
            if (this.rest == null && otherMethod.rest != null) {
                return false;
            }
            // If this rest is not null, both rests must have equal syntax
            if (this.rest != null && !this.rest.syntaxEquals(otherMethod.rest)) {
                return false;
            }
            return this.type.syntaxEquals(otherMethod.type)
                    && this.identifier.equals(otherMethod.identifier)
                    && Utils.syntaxEquals(this.parameters, otherMethod.parameters)
                    && this.block.syntaxEquals(otherMethod.block);
        }
        return false;
    }
}
