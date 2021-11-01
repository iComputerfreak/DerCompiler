package de.dercompiler.actions;

import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;

import java.io.Reader;

public class ParseTestAction extends Action {

    private static final String ACTION_ID = "parsetest";
    private final Reader reader;

    /**
     * Creates a new LexTestAction with the given input reader
     *
     * @param reader The input reader to read characters from
     */
    public ParseTestAction(Reader reader) {
        this.reader = reader;
    }


    @Override
    public void run() {
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        parser.parseProgram();
    }

    @Override
    public void help() {

    }

    @Override
    public String actionId() {
        return ACTION_ID;
    }
}
