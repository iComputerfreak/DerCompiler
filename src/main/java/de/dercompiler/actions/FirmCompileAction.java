package de.dercompiler.actions;

import de.dercompiler.Program;
import de.dercompiler.intermediate.CodeGenerationErrorIds;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.linker.Gcc;
import de.dercompiler.linker.ToolchainUtil;
import de.dercompiler.optimization.ArithmeticOptimization;
import de.dercompiler.optimization.GraphOptimization;
import de.dercompiler.optimization.PhiOptimization;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.PassManager;
import de.dercompiler.pass.PassManagerBuilder;
import de.dercompiler.util.ErrorStatus;
import firm.Backend;
import firm.Graph;
import firm.Util;

import java.io.IOException;
import java.util.List;

public class FirmCompileAction extends Action {
    private static final String compilerName = "DerCompiler";

    // The input file containing the source code to compile
    private final Source source;
    private boolean basicOptimizationsActive;

    /**
     * Creates a new CompileAction with the given source code file
     *
     * @param source The Source containing the MiniJava source code
     */
    public FirmCompileAction(Source source) {
        this.source = source;
        this.basicOptimizationsActive = false;
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

        if (basicOptimizationsActive) {
            List<GraphOptimization> opts = List.of(new ArithmeticOptimization(), new PhiOptimization());
            for (Graph graph : firm.Program.getGraphs()) {
                opts.forEach(opt -> opt.runOnGraph(graph));
            }
        }

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
        gcc.compileAndLink(base);
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

    public void setBasicOptimizationActive(boolean active) {
        this.basicOptimizationsActive = active;
    }
}
