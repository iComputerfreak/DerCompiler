package de.dercompiler.ast.statement;

import java.util.LinkedList;
import java.util.List;

public final class BasicBlock extends Statement {

    private LinkedList<Statement> statements;

    public BasicBlock() {
        statements = new LinkedList<>();
    }

    public BasicBlock(List<Statement> statements) {
        this();
        for(Statement s : statements) {
            this.statements.addLast(s);
        }
    }

    public void addStatement(Statement statement) {
        statements.addLast(statement);
    }
}
