package de.dercompiler.parser;

import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.SourcePosition;
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

    public SourcePosition position() {
        return lexer.peek().position();
    }

    public <T> T consumeToken(T passthrough) {
        nextToken();
        return passthrough;
    }

    public Lexer getLexer() {
        return lexer;
    }
}
