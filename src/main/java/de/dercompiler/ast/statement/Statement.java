package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.Method;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class Statement extends ASTNode permits BasicBlock, EmptyStatement, ErrorStatement, ExpressionStatement, IfStatement, LocalVariableDeclarationStatement, ReturnStatement, WhileStatement {

    public Method surrounding;

    public Statement(SourcePosition position) {
        super(position);
    }

    public void setSurroundingMethod(Method method) {
        surrounding = method;
    }

    public Method setSurroundingMethod() {
        return surrounding;
    }
}
