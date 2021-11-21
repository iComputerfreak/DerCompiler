package de.dercompiler.ast.expression;


import de.dercompiler.ast.ASTDefinition;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

/**
 * This class represents a global Object that is available in the compiled code without definition; it may be overridden by local code.
 * Used for 'System'.
 */
public final class GlobalConstant implements ASTDefinition {
    private Type type;

    public GlobalConstant(String name) {
        this.type = new Type(null, new CustomType(null, name), 0);
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public SourcePosition getSourcePosition() {
        return null;
    }
}
