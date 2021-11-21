package de.dercompiler.actions;

import de.dercompiler.ast.Program;
import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.general.GeneralWarningIds;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.PassManager;
import de.dercompiler.pass.PassManagerBuilder;
import de.dercompiler.pass.passes.*;
import de.dercompiler.util.ErrorStatus;

import java.io.File;

/**
 * Represents the action to compile the given source code
 */
public class CompileAction extends Action {
    
    private static final String compilerName = "DerCompiler";
    
    // The input file containing the source code to compile
    private final Source source;

    /**
     * Creates a new CompileAction with the given source code file
     * @param source The Source containing the MiniJava source code
     */
    public CompileAction(Source source) {
        this.source = source;
    }

    public void run() {
        //Step 1: build AST
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        ErrorStatus.exitProgramIfError();

        //Step 2: check AST
        PassManager manager = new PassManager(lexer);
        PassManagerBuilder.buildSemanticsPipeline(manager);
        manager.run(program);

        ErrorStatus.exitProgramIfError();

        //Step 3: Transformation

    }

    public void help() {
        CommandLineBuilder.printHelp(compilerName);

    }

    public String actionId() {
        return "compile";
    }
}
