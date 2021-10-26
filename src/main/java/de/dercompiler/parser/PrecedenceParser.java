package de.dercompiler.parser;

import de.dercompiler.lexer.IToken;
import de.dercompiler.lexer.Lexer;

public class PrecedenceParser {

    Lexer lexer;

    public PrecedenceParser(Lexer lexer) {
        this.lexer = lexer;
    }

    private int presidencOfOperation(IToken token) {

        return 0;
    }
}
