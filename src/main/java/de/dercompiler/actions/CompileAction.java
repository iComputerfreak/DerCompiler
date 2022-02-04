package de.dercompiler.actions;

import de.dercompiler.Function;
import de.dercompiler.Program;
import de.dercompiler.intermediate.CodeGenerationErrorIds;
import de.dercompiler.intermediate.generation.AtntCodeGenerator;
import de.dercompiler.intermediate.generation.CodeGenerator;
import de.dercompiler.intermediate.memory.BasicMemoryManager;
import de.dercompiler.intermediate.memory.SimpleMemoryManager;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.ordering.MyBlockSorter;
import de.dercompiler.intermediate.regalloc.LifetimeOptimizedRegisterAllocator;
import de.dercompiler.intermediate.regalloc.RegisterAllocator;
import de.dercompiler.intermediate.regalloc.TrivialRegisterAllocator;
import de.dercompiler.intermediate.regalloc.calling.AMDSystemVCallingConvention;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;
import de.dercompiler.intermediate.regalloc.calling.MicrosoftX86CallingConvention;
import de.dercompiler.intermediate.selection.BasicBlockGraph;
import de.dercompiler.intermediate.selection.CodeSelector;
import de.dercompiler.intermediate.selection.FirmBlock;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.linker.Gcc;
import de.dercompiler.linker.ToolchainUtil;
import de.dercompiler.optimization.ArithmeticOptimization;
import de.dercompiler.optimization.ConstantPropagation.TransferFunction;
import de.dercompiler.optimization.ConstantPropagation.Worklist;
import de.dercompiler.optimization.GraphOptimization;
import de.dercompiler.optimization.PhiOptimization;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.PassManager;
import de.dercompiler.pass.PassManagerBuilder;
import de.dercompiler.transformation.GraphDumper;
import de.dercompiler.transformation.TargetTriple;
import de.dercompiler.util.ErrorStatus;
import firm.Graph;

import java.util.List;

/**
 * Represents the action to compile the given source code
 */
public class CompileAction extends Action {
    
    private static final String compilerName = "DerCompiler";
    
    // The input file containing the source code to compile
    private final Source source;
    private boolean basicOptimizationsActive;
    private boolean optimizationActive;

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
        
        //Step 2: check AST & Transformation
        PassManager manager = new PassManager(lexer);
        PassManagerBuilder.buildTransformationPipeline(manager);

        manager.run(program);

        ErrorStatus.exitProgramIfError();

        //Step 3: Code Selection
        CallingConvention convention;
        if (TargetTriple.isWindows()) {
          convention = new MicrosoftX86CallingConvention();
        } else {
            convention = new AMDSystemVCallingConvention();
        }


        List<GraphOptimization> opts = List.of(new ArithmeticOptimization(), new PhiOptimization());
        MyBlockSorter sorter = new MyBlockSorter();
        RegisterAllocator allocator = optimizationActive ?
                new LifetimeOptimizedRegisterAllocator(convention) :
                new TrivialRegisterAllocator(new BasicMemoryManager(), convention);
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
            // with flatMap, firmBlocks went missing - strange.
            List<Operation> operations = firmBlocks.stream().flatMap((FirmBlock firmBlock) -> firmBlock.getOperations().stream()).toList();
            function.setOperations(operations);
            function.setVrCount(VirtualRegister.getNextID());
            allocator.allocateRegisters(function);
        }

        ErrorStatus.exitProgramIfError();

        CodeGenerator gen = new AtntCodeGenerator();

        String base = ToolchainUtil.getBaseName(source.filename());
        gen.createAssembler(program, ToolchainUtil.appendAssemblerExtension(base));

        Gcc gcc = new Gcc("gcc");
        if (!gcc.checkCompiler()) {
            compilerError();
            return; //we never return
        }
        gcc.compileFirm(base);
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
