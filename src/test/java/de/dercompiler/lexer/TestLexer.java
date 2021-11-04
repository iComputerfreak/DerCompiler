package de.dercompiler.lexer;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;

import java.io.File;

/**
 *  Represents a {@link Lexer} with little extra functionality for testing, leaving the core functionality untouched.
 */
public class TestLexer extends Lexer {

    public TestLexer(Source source) {
        super(source);
    }

    public void fail(LexerErrorIds id, String message) {
        new OutputMessageHandler(MessageOrigin.LEXER).printErrorAndContinue(id, message);
        throw new LexerException(message);
    }

    public static TestLexer forFile(File file) {
        return new TestLexer(Source.forFile(file));
    }

    public static TestLexer forString(String input) {
        return new TestLexer(Source.forString(input));
    }

    public static class LexerException extends RuntimeException {

        public LexerException(String message) {
            super(message);
        }

    }

}
