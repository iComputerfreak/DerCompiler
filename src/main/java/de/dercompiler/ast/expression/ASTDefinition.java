package de.dercompiler.ast.expression;

import de.dercompiler.semantic.type.TypeFactory;

public interface ASTDefinition {

    de.dercompiler.ast.type.Type getType();

    default de.dercompiler.semantic.type.Type getRefType() {
        return TypeFactory.getInstance().create(this.getType());
    }
}

