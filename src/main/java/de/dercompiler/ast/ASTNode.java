package de.dercompiler.ast;

import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.statement.Statement;

public interface ASTNode {

    public abstract boolean syntaxEqual(ASTNode other);
}
