package de.dercompiler.ast.expression;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Field;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;

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
}

