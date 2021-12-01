package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class Statement extends ASTNode permits BasicBlock, EmptyStatement, ErrorStatement, ExpressionStatement, IfStatement, LocalVariableDeclarationStatement, ReturnStatement, WhileStatement {

    public Method surrounding;
    public Statement surround;

    public Statement(SourcePosition position) {
        super(position);
        surround = null;
        surrounding = null;
    }

    public void setSurroundingMethod(Method method) {
        surrounding = method;
    }

    public Method getSurroundingMethod() {
        return surrounding;
    }

    public void setSurroundingStatement(Statement surrounding) {
        surround = surrounding;
    }

    public Statement getSurroundingStatement() {
        return surround;
    }

    public abstract void accept(ASTStatementVisitor astStatementVisitor);

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        this.accept((ASTStatementVisitor) astNodeVisitor);
    }
}
