package de.dercompiler.actions;

import de.dercompiler.io.Source;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;

import java.io.Reader;

import static de.dercompiler.io.CommandLineStrings.*;

public class ParseTestAction extends Action {

    private static final String ACTION_ID = "parsetest";
    private final Source source;
    private final String entryPoint;

    /**
     * Creates a new LexTestAction with the given input source
     *
     * @param source The input source to read characters from
     * @param entryPoint The level of the grammar to enter the parsing process
     */
    public ParseTestAction(Source source, String entryPoint) {
        this.source = source;
        this.entryPoint = entryPoint;
    }


    @Override
    public void run() {
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        if (this.entryPoint == null) {
            parser.parseProgram();
            return;
        }

        switch (this.entryPoint) {
            case OPTION_PARSE_METHOD:
                parser.parseFullMethod();
                break;
            case OPTION_PARSE_STATEMENT:
                parser.parseBlockStatement();
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
