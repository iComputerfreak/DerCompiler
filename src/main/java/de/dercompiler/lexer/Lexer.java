package de.dercompiler.lexer;

import parser.Parser;

import static de.dercompiler.lexer.Token.*;

import java.io.File;

public class Lexer {

    private RingBuffer<IToken> tokenBuffer;
    private static final int SLL_CONSTANT = 4;

    public Lexer(File input) {
        this.tokenBuffer = new RingBuffer<>(SLL_CONSTANT);
    }

    public IToken nextToken() {
        return tokenBuffer.pop();
        // TODO: enqueue another token
    }

    public IToken peek(int lookAhead) {
        return tokenBuffer.peek(lookAhead);
    }

    private void push(IToken token) {
        // TODO
    }

}
