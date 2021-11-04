package de.dercompiler.ast;

import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.type.Type;

public final class MainMethod extends ClassMember {
    
    private final String identifier;
    private final Type parameterType;
    private final String parameterName;
    private final MethodRest methodRest;
    private final BasicBlock block;
    
    // INFO: methodRest may be null
    public MainMethod(String identifier, Type parameterType, String parameterName, MethodRest methodRest, BasicBlock block) {
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
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (other instanceof MainMethod otherMain) {
            return this.identifier.equals(otherMain.identifier)
                    && this.parameterType.syntaxEquals(otherMain.parameterType)
                    && this.parameterName.equals(otherMain.parameterName)
                    && this.methodRest.syntaxEquals(otherMain.methodRest)
                    && this.block.syntaxEquals(otherMain.block);
        }
        return false;
    }
}
