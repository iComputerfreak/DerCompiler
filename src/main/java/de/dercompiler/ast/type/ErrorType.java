package de.dercompiler.ast.type;

import de.dercompiler.lexer.SourcePosition;

public final class ErrorType extends BasicType {
    public ErrorType(SourcePosition position) {
        super(position);
    }
}
