package de.dercompiler.actions;

import de.dercompiler.ast.Program;
import de.dercompiler.intermediate.Function;
import de.dercompiler.intermediate.memory.BasicMemoryManager;
import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.ordering.MyBlockSorter;
import de.dercompiler.intermediate.regalloc.TrivialRegisterAllocator;
import de.dercompiler.intermediate.selection.BasicBlockGraph;
import de.dercompiler.intermediate.selection.CodeSelector;
import de.dercompiler.intermediate.selection.FirmBlock;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.Source;
import de.dercompiler.lexer.Lexer;
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

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Represents the action to compile the given source code
 */
public class CompileAction extends Action {
    
    private static final String compilerName = "DerCompiler";
    
    // The input file containing the source code to compile
    private final Source source;
    private boolean basicOptimizationsActive;

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

        GraphDumper.dump(true);

        manager.run(program);

        ErrorStatus.exitProgramIfError();

        //Step 3: Code Selection


        List<GraphOptimization> opts = List.of(new ArithmeticOptimization(), new PhiOptimization());
        for (firm.Graph graph : program.getGraphs()) {
            if (basicOptimizationsActive) {
                opts.forEach(opt -> opt.runOnGraph(graph));
                Worklist.run(new TransferFunction(),graph);
            }

            CodeSelector selector = new CodeSelector(graph);
            BasicBlockGraph blocksGraph = selector.generateCode();
            GraphDumper.dumpBlocksGraph(blocksGraph.getGraph(), graph.toString().substring(6) + "-finalBlocks");

            //Step 4: Block ordering

            MyBlockSorter sorter = new MyBlockSorter();
            List<FirmBlock> firmBlocks = sorter.sortBlocks(blocksGraph);
            Function f = new Function(graph.toString(), firmBlocks.stream().flatMap(b -> b.getOperations().stream()).toList());


            System.out.println("\n\nchained IR firm blocks:\n");


            StringJoiner joiner = new StringJoiner("\n");
            for (FirmBlock firmBlock : firmBlocks) {
                String operations = firmBlock.getText();
                joiner.add(operations);
            }
            System.out.println(joiner);

            System.out.println("\n\nx64 code:\n");
            new TrivialRegisterAllocator(new BasicMemoryManager()).allocateRegisters(f);
        }
        
        ErrorStatus.exitProgramIfError();

    }

    public void help() {
        CommandLineBuilder.printHelp(compilerName);

    }

    public void setBasicOptimizationActive(boolean active) {
        this.basicOptimizationsActive = active;
    }

    public String actionId() {
        return "compile";
    }
}
