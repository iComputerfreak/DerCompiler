package de.dercompiler.parser;

import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.token.IToken;

public class LexerWrapper {

    private Lexer lexer;

    public LexerWrapper(Lexer lexer) {
        this.lexer = lexer;
    }

    public IToken nextToken() {
        lexer.nextToken();
        return lexer.peek().type();
    }

    public IToken peek() {
        return lexer.peek().type();
    }

    public IToken peek(int position) {
        return lexer.peek(position).type();
    }

    public Lexer.Position position() {
        return lexer.getPosition();
    }
}