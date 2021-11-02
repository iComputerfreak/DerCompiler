package de.dercompiler.actions;

import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;

import java.io.Reader;

import static de.dercompiler.io.CommandLineStrings.*;

public class ParseTestAction extends Action {

    private static final String ACTION_ID = "parsetest";
    private final Reader reader;
    private final String entryPoint;

    /**
     * Creates a new LexTestAction with the given input reader
     *
     * @param reader The input reader to read characters from
     * @param entryPoint The level of the grammar to enter the parsing process
     */
    public ParseTestAction(Reader reader, String entryPoint) {
        this.reader = reader;
        this.entryPoint = entryPoint;
    }


    @Override
    public void run() {
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        if (this.entryPoint == null) {
            parser.parseProgram();
            return;
        }

        switch (this.entryPoint) {
            case OPTION_PARSE_METHOD:
                parser.parseMethod();
                break;
            case OPTION_PARSE_STATEMENT:
                parser.parseStatement();
                break;
            case OPTION_PARSE_EXPRESSION:
                parser.parseExpression();
                break;
            default:
                break;
        }
    }

    @Override
    public void help() {

    }

    @Override
    public String actionId() {
        return ACTION_ID;
    }
}
