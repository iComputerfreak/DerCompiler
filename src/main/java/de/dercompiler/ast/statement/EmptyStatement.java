package de.dercompiler.ast.statement;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class EmptyStatement extends Statement {

    public EmptyStatement() {

    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof EmptyStatement) return true;
        return false;
    }
}
