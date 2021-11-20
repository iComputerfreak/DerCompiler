package de.dercompiler.ast.expression;


import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.Type;

public class GlobalConstant implements ASTDefinition {
    private Type type;

    public GlobalConstant(String name) {
        this.type = new Type(null, new CustomType(null, name), 0);
    }

    @Override
    public Type getType() {
        return this.type;
    }
}
