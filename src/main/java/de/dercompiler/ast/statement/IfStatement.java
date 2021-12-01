package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class IfStatement extends Statement {

    Expression condition;
    Statement thenStatement;
    Statement elseStatement;

    public IfStatement(SourcePosition position, Expression condition, Statement thenStatement, Statement elseStatement) {
        super(position);
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    public boolean hasElse() {
        return Objects.nonNull(elseStatement);
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getThenStatement() {
        return thenStatement;
    }

    public Statement getElseStatement() {
        return elseStatement;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof IfStatement is) {
            boolean result = condition.syntaxEquals(is.condition) && thenStatement.syntaxEquals(is.thenStatement);
            if (hasElse() && is.hasElse()) {
                return result && elseStatement.syntaxEquals(is.elseStatement);
                //xor hasElse() and is.hasElse() results in true, if one is null and the other is not, so ifs are not equal
            } else if (hasElse() ^ is.hasElse()) {
                return false;
            } else {
                return result;
            }
        }
        return false;
    }

    @Override
    public void accept(ASTStatementVisitor astStatementVisitor) {
        astStatementVisitor.visitIfStatement(this);
    }

}
