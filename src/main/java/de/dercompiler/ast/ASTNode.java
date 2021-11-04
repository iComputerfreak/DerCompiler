package de.dercompiler.ast;

import de.dercompiler.lexer.SourcePosition;

public interface ASTNode {

    boolean syntaxEquals(ASTNode other);

    //SourcePosition getSourcePosition();

}
