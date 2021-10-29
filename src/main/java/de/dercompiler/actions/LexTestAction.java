package de.dercompiler.actions;

import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.TokenOccurrence;
import de.dercompiler.lexer.token.Token;

import java.io.File;

public class LexTestAction extends Action {

    public static final String HELP_TEXT = "--lexTest <file>: Generates a sequence of tokens out of the file and prints it to the console.";
    public static final String COMMAND_LINE_NAME = "lexTest";
    private final boolean printPosition;
    private File input;

    /**
     * Creates a new LexTestAction with the given input file
     * @param input The file to lex
     * @param printPosition If true, the positions of the tokens are printed out.
     */
    public LexTestAction(File input, boolean printPosition) {
        this.input = input;
        this.printPosition = printPosition;
    }

    @Override
    public void run() {
        Lexer lexer = new Lexer(this.input);
        TokenOccurrence token;
        do {
            token = lexer.nextToken();
            String output = this.printPosition ? "%6s %s".formatted(token.position(), token.type())
                    : token.type().toString();
            System.out.println(output);
        } while (token.type() != Token.EOF);
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
