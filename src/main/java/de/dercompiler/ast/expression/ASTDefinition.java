package de.dercompiler.ast.expression;

import de.dercompiler.ast.Field;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.semantic.type.Type;
import de.dercompiler.semantic.type.TypeFactory;

public interface ASTDefinition {

    enum DefinitionType {
        PARAMETER, FIELD, LOCAL_VARIABLE;
    }

    public abstract DefinitionType getDefinitionType();

    public boolean isParameter();
    public Parameter getParameter();

    public boolean isField();
    public Field getField();

    public boolean isLocalVariable();
    public LocalVariableDeclarationStatement getLocalVariable();

    public de.dercompiler.ast.type.Type getType();

    public default de.dercompiler.semantic.type.Type getRefType() {
        return TypeFactory.getInstance().create(this.getType());
    }
}

