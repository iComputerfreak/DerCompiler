package de.dercompiler.actions;

import de.dercompiler.Function;
import de.dercompiler.Program;
import de.dercompiler.intermediate.CodeGenerationErrorIds;
import de.dercompiler.intermediate.generation.CodeGenerator;
import de.dercompiler.intermediate.generation.IntelCodeGenerator;
import de.dercompiler.intermediate.memory.BasicMemoryManager;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.ordering.MyBlockSorter;
import de.dercompiler.intermediate.regalloc.RegisterAllocator;
import de.dercompiler.intermediate.regalloc.TrivialRegisterAllocator;
import de.dercompiler.intermediate.regalloc.calling.MicrosoftX86CallingConvention;
import de.dercompiler.intermediate.selection.BasicBlockGraph;
import de.dercompiler.intermediate.selection.CodeSelector;
import de.dercompiler.intermediate.selection.FirmBlock;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.linker.*;
import de.dercompiler.optimization.ArithmeticOptimization;
import de.dercompiler.optimization.ConstantPropagation.TransferFunction;
import de.dercompiler.optimization.ConstantPropagation.Worklist;
import de.dercompiler.optimization.GraphOptimization;
import de.dercompiler.optimization.PhiOptimization;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.PassManager;
import de.dercompiler.pass.PassManagerBuilder;
import de.dercompiler.transformation.GraphDumper;
import de.dercompiler.util.ErrorStatus;
import firm.Graph;

import java.util.List;

public class WindowsCompileAction extends Action {


    private static final String compilerName = "DerCompiler";

    // The input file containing the source code to compile
    private final Source source;
    private boolean basicOptimizationsActive;
    private boolean optimizationActive;

    /**
     * Creates a new CompileAction with the given source code file
     * @param source The Source containing the MiniJava source code
     */
    public WindowsCompileAction(Source source) {
        this.source = source;
    }

    public void run() {
        //Step 1: build AST
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        ErrorStatus.exitProgramIfError();

        //Step 2: check AST & Transformation
        PassManager manager = new PassManager(lexer);
        PassManagerBuilder.buildTransformationPipeline(manager);

        manager.run(program);

        ErrorStatus.exitProgramIfError();

        //Step 3: Code Selection


        List<GraphOptimization> opts = List.of(new ArithmeticOptimization(), new PhiOptimization());
        MyBlockSorter sorter = new MyBlockSorter();
        RegisterAllocator allocator = new TrivialRegisterAllocator(new BasicMemoryManager(), new MicrosoftX86CallingConvention());
        for (Function function : program.getFunctions()) {
            VirtualRegister.resetNextID();
            Graph graph = function.getFirmGraph();
            if (basicOptimizationsActive) {
                opts.forEach(opt -> opt.runOnGraph(graph));
                Worklist.run(new TransferFunction(), graph);
            }

            CodeSelector selector = new CodeSelector(graph);
            BasicBlockGraph blocksGraph = selector.generateCode();
            GraphDumper.dumpBlocksGraph(blocksGraph.getGraph(), graph.toString().substring(6) + "-finalBlocks");

            //Step 4: Block ordering

            List<FirmBlock> firmBlocks = sorter.sortBlocks(blocksGraph);
            List<Operation> operations = firmBlocks.stream().flatMap((FirmBlock firmBlock) -> firmBlock.getOperations().stream()).toList();
            function.setOperations(operations);
            function.setVrCount(VirtualRegister.getNextID());
            allocator.allocateRegisters(function);
        }

        ErrorStatus.exitProgramIfError();

        ExternalToolchain.setAssemblerStyle(AssemblerStyle.Intel);

        CodeGenerator gen = new IntelCodeGenerator();

        String base = ToolchainUtil.getBaseName(source.filename());
        gen.createAssembler(program, ToolchainUtil.appendAssemblerExtension(base));

        Clang clang = new Clang("clang");
        if (!clang.checkCompiler()) {
            compilerError();
            return; //we never return
        }
        clang.compileAndLink(base);
    }

    public void help() {
        CommandLineBuilder.printHelp(compilerName);

    }

    private void compilerError() {
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.COMPILER_ERROR, "Error while try to run gcc");
    }

    public void setBasicOptimizationActive(boolean active) {
        this.basicOptimizationsActive = active;
    }

    public String actionId() {
        return "compile";
    }

    public void setOptimizationActive(boolean optimizationActive) {
        this.optimizationActive = optimizationActive;
    }

    public boolean getOptimizationActive() {
        return optimizationActive;
    }
}
