package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class Statement extends ASTNode permits BasicBlock, EmptyStatement, ErrorStatement, ExpressionStatement, IfStatement, LocalVariableDeclarationStatement, ReturnStatement, WhileStatement {

    public Method surrounding;
    public Statement surround;

    private boolean dead;
    private boolean hasReturn;

    public Statement(SourcePosition position) {
        super(position);
        surround = null;
        surrounding = null;
        dead = false;
        hasReturn = false;
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

    public void markDead() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public void returns() {
        hasReturn = true;
    }

    public boolean hasReturn() {
        return hasReturn;
    }

    public abstract void accept(ASTStatementVisitor astStatementVisitor);

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        this.accept((ASTStatementVisitor) astNodeVisitor);
    }
}
