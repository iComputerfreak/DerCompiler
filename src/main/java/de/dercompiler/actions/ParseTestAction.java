package de.dercompiler.actions;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.PrettyPrinter;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.AnchorSet;
import de.dercompiler.parser.Parser;

import static de.dercompiler.io.CommandLineStrings.*;

public class ParseTestAction extends Action {

    private static final String ACTION_ID = "parsetest";
    private final Source source;
    private final String entryPoint;
    private boolean prettyPrint = false;

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
        ASTNode node = null;
        if (this.entryPoint == null) {
            node = parser.parseProgram();
        } else {

        switch (this.entryPoint) {
            case OPTION_PARSE_METHOD:
                node = parser.parseFullMethod(new AnchorSet());
                break;
            case OPTION_PARSE_STATEMENT:
                node = parser.parseBlockStatement(new AnchorSet());
                break;
            case OPTION_PARSE_EXPRESSION:
                node = parser.parseExpression(new AnchorSet());
                break;
            default:
                break;
        }}
        if (this.prettyPrint) {
            PrettyPrinter printer = new PrettyPrinter(false);
            printer.visitNode(node);
            new OutputMessageHandler(MessageOrigin.GENERAL).printPlane(printer.flush());
        }
    }

    @Override
    public void help() {

    }

    @Override
    public String actionId() {
        return ACTION_ID;
    }

    public void setPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }
}
