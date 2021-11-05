package de.dercompiler.ast;

import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

public final class MainMethod extends ClassMember {

    private final String identifier;
    private final Type parameterType;
    private final String parameterName;
    private final MethodRest methodRest;
    private final BasicBlock block;
    
    // INFO: methodRest may be null
    public MainMethod(SourcePosition position, String identifier, Type parameterType, String parameterName, MethodRest methodRest, BasicBlock block) {
        super(position);
        this.identifier = identifier;
        this.parameterType = parameterType;
        this.parameterName = parameterName;
        this.methodRest = methodRest;
        this.block = block;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Type getParameterType() {
        return parameterType;
    }

    public String getParameterName() {
        return parameterName;
    }

    public MethodRest getMethodRest() {
        return methodRest;
    }

    public BasicBlock getBlock() {
        return block;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof MainMethod otherMain) {
            // If this rest is null, but the other is not, return false
            if (this.methodRest == null && otherMain.methodRest != null) {
                return false;
            }
            // If this rest is not null, both rests must have equal syntax
            if (this.methodRest != null && !this.methodRest.syntaxEquals(otherMain.methodRest)) {
                return false;
            }
            return this.identifier.equals(otherMain.identifier)
                    && this.parameterType.syntaxEquals(otherMain.parameterType)
                    && this.parameterName.equals(otherMain.parameterName)
                    && this.block.syntaxEquals(otherMain.block);
        }
        return false;
    }
}
