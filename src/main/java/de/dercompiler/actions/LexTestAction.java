package de.dercompiler.actions;

import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.token.Token;

import java.io.File;

public class LexTestAction extends Action {

    public static final String HELP_TEXT = "--lexTest <file>: Generates a sequence of tokens out of the file and prints it to the console.";
    public static final String COMMAND_LINE_NAME = "lexTest";
    private File input;

    /**
     * Creates a new LexTestAction with the given input file
     * @param input The file to lex
     */
    public LexTestAction(File input) {
        this.input = input;
    }

    @Override
    public void run() {
        Lexer lexer = new Lexer(this.input);
        IToken token;
        do {
            token = lexer.nextToken();
            System.out.println(token);
        } while (token != Token.EOF);
    }

    @Override
    public void help() {
        System.out.println(HELP_TEXT);
    }

    @Override
    public String actionId() {
        return COMMAND_LINE_NAME;
    }
}
