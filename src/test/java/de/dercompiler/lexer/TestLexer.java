package de.dercompiler.lexer;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.io.*;

/**
 *  Represents a {@link Lexer} with little extra functionality for testing, leaving the core functionality untouched.
 */
public class TestLexer extends Lexer {

    public TestLexer(Reader reader) {
        super(reader);
    }

    public void fail(LexerErrorIds id, String message) {
        new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndContinue(id, message);
        throw new LexerException(message);
    }

    public static TestLexer forFile(File file) {
        try {
            return new TestLexer(new FileReader(file));
        } catch (FileNotFoundException e) {
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err).printErrorAndExit(GeneralErrorIds.FILE_NOT_FOUND, "Could not lex file: file not found or not readable.");
        }
        return null;
    }

    public static TestLexer forString(String input) {
        return new TestLexer(new StringReader(input));
    }

    public static class LexerException extends RuntimeException {

        public LexerException(String message) {
            super(message);
        }

    }

}
