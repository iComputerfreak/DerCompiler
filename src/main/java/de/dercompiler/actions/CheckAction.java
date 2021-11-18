package de.dercompiler.actions;

import de.dercompiler.ast.Program;
import de.dercompiler.io.Source;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.PassManager;
import de.dercompiler.pass.passes.*;

public class CheckAction extends Action {

    private static final String ACTION_ID = "check";
    private Source source;

    public CheckAction(Source src) {
        this.source = src;
    }

    public void run() {
        Lexer lexer = new Lexer(this.source);
        Program program = new Parser(lexer).parseProgram();

        PassManager manager = new PassManager();

        //name-analysis passes
        manager.addPass(new InterClassAnalysisCheckPass());
        manager.addPass(new EnterScopePass());
        manager.addPass(new VariableAnalysisCheckPass());
        manager.addPass(new LeaveScopePass());

        //type-analysis passes
        manager.addPass(new TypeAnalysisPass());

        manager.run(program);
    }

    @Override
    public void help() {

    }

    @Override
    public String actionId() {
        return ACTION_ID;
    }
}
