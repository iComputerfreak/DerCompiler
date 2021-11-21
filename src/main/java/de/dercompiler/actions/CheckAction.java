package de.dercompiler.actions;

import de.dercompiler.ast.Program;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.PassManager;
import de.dercompiler.pass.passes.*;

public class CheckAction extends Action {

    private static final String ACTION_ID = "check";
    private Source source;
    private boolean printTypeAnnotation;

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

        // name-analysis passes
        manager.addPass(new InterClassAnalysisCheckPass());
        manager.addPass(new MethodDeclarationPass());
        manager.addPass(new EnterScopePass());
        manager.addPass(new VariableAnalysisCheckPass());
        manager.addPass(new LeaveScopePass());

        // type-analysis passes
        manager.addPass(new TypeAnalysisPass());

        // specification-related passes
        manager.addPass(new SpecificationConformityPass());

        manager.run(program);
        if (this.printTypeAnnotation) {
            TypeAnnotationPrinter typeAnnotationPrinter = new TypeAnnotationPrinter(true);
            typeAnnotationPrinter.visitProgram(program);
            System.out.println(typeAnnotationPrinter.flush());
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
