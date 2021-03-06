package de.dercompiler.actions;

import de.dercompiler.Program;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.PassManager;
import de.dercompiler.pass.PassManagerBuilder;
import de.dercompiler.pass.passes.*;

public class CheckAction extends Action {

    private static final String ACTION_ID = "check";
    private final Source source;
    private final boolean printTypeAnnotation;

    public CheckAction(Source src, boolean printTypeAnnotation) {
        this.source = src;
        this.printTypeAnnotation = printTypeAnnotation;
    }

    public void run() {
        Lexer lexer = new Lexer(this.source);
        Program program = new Parser(lexer).parseProgram();

        if (!OutputMessageHandler.getEvents().isEmpty()) {
            return;
        }

        PassManager manager = new PassManager(lexer);
        PassManagerBuilder.buildSemanticsPipeline(manager);
        manager.run(program);

        if (this.printTypeAnnotation) {
            TypeAnnotationPrinter typeAnnotationPrinter = new TypeAnnotationPrinter(true);
            typeAnnotationPrinter.visitProgram(program);
            new OutputMessageHandler(MessageOrigin.GENERAL).printInfo("\n" + typeAnnotationPrinter.flush());
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
