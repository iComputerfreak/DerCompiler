package de.dercompiler.lexer;

import parser.Parser;

import static de.dercompiler.lexer.Token.*;

public class Lexer {

    private static final int SLL_K = 4;

    private IToken buffer[] = new IToken[SLL_K];
    private int index;

    private IToken lex() {
        return T_EOF;
    }

    public IToken next_Token() {
        return T_EOF;
    }

    public IToken get(int index) {
        if (index < 0 || index >= SLL_K) {
            //internal error
        }
        return T_EOF;
    }

}
