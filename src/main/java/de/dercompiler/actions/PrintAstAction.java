package de.dercompiler.actions;

import de.dercompiler.Program;
import de.dercompiler.ast.printer.PrettyPrinter;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;

public class PrintAstAction extends Action {


    private static final String ACTION_ID = "print-ast";
    private final Source source;

    public PrintAstAction(Source src) {
        this.source = src;
    }

    @Override
    public void run() {
        Lexer lexer = new Lexer(this.source);
        Program program = new Parser(lexer).parseProgram();
        PrettyPrinter prettyPrinter = new PrettyPrinter(true);
        prettyPrinter.visitNode(program);
        new OutputMessageHandler(MessageOrigin.GENERAL).printPlain(prettyPrinter.flush());
    }

    @Override
    public void help() {

    }

    @Override
    public String actionId() {
        return ACTION_ID;
    }
}
