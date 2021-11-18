package de.dercompiler.ast.expression;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Field;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;

interface ASTDefinition {

    enum DefinitionType {
        CLASS, METHOD, PARAMETER, FIELD, LOCAL_VARIABLE;
    }

    public abstract DefinitionType getDefinitionType();

    public boolean isClassMember();
    public ClassDeclaration getClassDeclaration(); // returns null if isClassMember() == false

    public boolean isMethod();
    public Method getMethod();

    public boolean isParameter();
    public Parameter getParameter();

    public boolean isField();
    public Field getField();

    public boolean isLocalVariable();
    public LocalVariableDeclarationStatement getLocalVariable();
}

