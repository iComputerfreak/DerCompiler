package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof BasicBlock bb) {
            boolean result = true;
            if (statements.size() != bb.statements.size()) return false;
            Iterator<Statement> itThis = statements.iterator();
            Iterator<Statement> itO = bb.statements.iterator();
            while(itThis.hasNext()) {
                Statement sttThis = itThis.next();
                Statement sttO = itO.next();
                result &= sttThis.syntaxEqual(sttO);
            }
            return result;
        }
        return false;
    }
}
