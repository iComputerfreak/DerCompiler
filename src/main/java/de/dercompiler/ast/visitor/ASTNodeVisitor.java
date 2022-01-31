package de.dercompiler.ast.visitor;

import de.dercompiler.Program;
import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.Type;

public interface ASTNodeVisitor extends ASTExpressionVisitor, ASTStatementVisitor {

    void visitArguments(Arguments arguments);
    void visitBasicType(BasicType basicType);
    void visitClassDeclaration(ClassDeclaration classDeclaration);
    void visitErrorClassMember(ErrorClassMember errorClassMember);
    void visitField(Field field);
    void visitMainMethod(MainMethod mainMethod);
    void visitMethod(Method method);
    void visitMethodRest(MethodRest methodRest);
    default void visitNode(ASTNode node) {
        node.accept(this);
    }
    void visitParameter(Parameter parameter);
    void visitProgram(Program program);

    void visitType(Type type);
    void visitUninitializedValue(UninitializedValue uninitializedValue);

}
