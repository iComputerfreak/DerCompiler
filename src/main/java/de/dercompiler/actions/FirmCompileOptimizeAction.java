package de.dercompiler.actions;

import de.dercompiler.ast.Program;
import de.dercompiler.generation.CodeGenerationErrorIds;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.linker.Gcc;
import de.dercompiler.linker.ToolchainUtil;
import de.dercompiler.optimization.ConstantPropagation.Worklist;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.PassManager;
import de.dercompiler.pass.PassManagerBuilder;
import de.dercompiler.util.ErrorStatus;
import firm.Backend;
import firm.Util;

import java.io.IOException;

public class FirmCompileOptimizeAction extends Action {
    private static final String compilerName = "DerCompiler";

    // The input file containing the source code to compile
    private final Source source;

    /**
     * Creates a new CompileAction with the given source code file
     *
     * @param source The Source containing the MiniJava source code
     */
    public FirmCompileOptimizeAction(Source source) {
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
        PassManagerBuilder.buildTransformationPipeline(manager);
        manager.run(program);
        ErrorStatus.exitProgramIfError();
        for (firm.Graph graph : program.getGraphs()) {
            new Worklist(null, graph);
        }
        ErrorStatus.exitProgramIfError();

        //lower members
        Util.lowerSels();
        //maybe add later
        Backend.lowerForTarget();
        String base = ToolchainUtil.getBaseName(source.filename());

        try {
            Backend.createAssembler(base + ".S", source.filename());
        } catch (IOException e) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.CANT_OUTPUT_FILE, "Can,t write output-file", e);
        }

        Gcc gcc = new Gcc("gcc");
        if (!gcc.checkCompiler()) {
            compilerError();
            return; //we never return
        }
        gcc.compileFirm(base);
    }

    private void compilerError() {
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.COMPILER_ERROR, "Error while try to run gcc");
    }

    public void help() {
        CommandLineBuilder.printHelp(compilerName);
    }

    public String actionId() {
        return "compileFirm";
    }
}
