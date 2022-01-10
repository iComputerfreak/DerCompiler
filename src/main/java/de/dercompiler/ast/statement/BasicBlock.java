package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class BasicBlock extends Statement {

    private final LinkedList<Statement> statements;
    private boolean hasReturn;

    public BasicBlock(SourcePosition position) {
        super(position);statements = new LinkedList<>();
        hasReturn = false;
    }

    public BasicBlock(SourcePosition position, List<Statement> statements) {
        this(position);
        for(Statement s : statements) {
            this.statements.addLast(s);
        }
    }

    public void addStatement(Statement statement) {
        statements.addLast(statement);
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof BasicBlock bb) {
            boolean result = true;
            if (statements.size() != bb.statements.size()) return false;
            Iterator<Statement> itThis = statements.iterator();
            Iterator<Statement> itO = bb.statements.iterator();
            while(itThis.hasNext()) {
                Statement sttThis = itThis.next();
                Statement sttO = itO.next();
                result &= sttThis.syntaxEquals(sttO);
            }
            return result;
        }
        return false;
    }

    @Override
    public void accept(ASTStatementVisitor astStatementVisitor) {
        astStatementVisitor.visitBasicBlock(this);
    }
}
