package de.dercompiler.actions;
import de.dercompiler.io.Source;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.TokenOccurrence;
import de.dercompiler.lexer.token.Token;

public class LexTestAction extends Action {

    public static final String HELP_TEXT = "--lexTest <file>: Generates a sequence of tokens out of the file and prints it to the console.";
    public static final String COMMAND_LINE_NAME = "lexTest";
    private final Source source;
    private boolean printPosition;

    /**
     * Creates a new LexTestAction with the given input source
     *
     * @param source The input source to read characters from
     */
    public LexTestAction(Source source) {
        this.source = source;
    }

    @Override
    public void run() {
        Lexer lexer = new Lexer(this.source);
        TokenOccurrence token;
        do {
            token = lexer.nextToken();
            String output = this.printPosition ? "%6s %s".formatted(token.position(), token.type())
                    : token.type().toString();
            System.out.println(output);
        } while (token.type() != Token.EOF);
    }

    public void setPrintPosition(boolean printPosition) {
        this.printPosition = printPosition;
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
