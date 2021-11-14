package de.dercompiler.pass;

import de.dercompiler.ast.Program;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;
import org.junit.jupiter.api.*;

import javax.print.attribute.standard.DocumentName;

import static org.junit.jupiter.api.Assertions.*;

public class PassDagSolverTest {

    public static final String testProgram =
"""
class pip { /* 1 */
    
    public int pop(int pup) /* 2 */ { /* 3 */
        int a = 0; /* 4 */ /* 5 */
        { /* 6 */
            int b = 1; /* 7 */ /* 8 */
        }
        int c = 2; /* 9 */ /* 10 */
    }
}
""";

    @BeforeAll
    static void setup() {
        OutputMessageHandler.setDebug();
        PassManager.setPrintPipeline(true);
    }

    @AfterEach
    void cleanup() {

    }

    @AfterAll
    static void clean() {
        PassManager.setPrintPipeline(false);
    }

    @Test
    @DisplayName("run check 1")
    void runCheck1() {
        Program program = new Parser(Lexer.forString(testProgram)).parseProgram();
        program.indexed();

        PassManager manager = new PassManager();
        final int[] count = {0};
        PassMockFactory.RunOnXY counter = new PassMockFactory.RunOnXY() {
            @Override
            public Void apply(Void unused) {
                count[0] += 1;
                return null;
            }
        };
        manager.addPass(PassMockFactory.generateClassPass(new AnalysisUsage(), AnalysisDirection.TOP_DOWN, counter));
        manager.addPass(PassMockFactory.generateMethodPass(new AnalysisUsage(), AnalysisDirection.TOP_DOWN, counter));
        manager.addPass(PassMockFactory.generateBlockPass(new AnalysisUsage(), AnalysisDirection.TOP_DOWN, counter));
        manager.addPass(PassMockFactory.generateStatementPass(new AnalysisUsage(), AnalysisDirection.TOP_DOWN, counter));
        manager.addPass(PassMockFactory.generateExpressionPass(new AnalysisUsage(), AnalysisDirection.TOP_DOWN, counter));

        manager.run(program);

        assertEquals(10, count[0]);
    }

    @Test
    @DisplayName("resolve DAG-1")
    void resolveDag1() {
        Program program = new Parser(Lexer.forString(testProgram)).parseProgram();
        PassManager manager = new PassManager();

        manager.addPass(new DAG1Passes.A_DAG1());
        manager.addPass(new DAG1Passes.B_DAG1());
        manager.addPass(new DAG1Passes.C_DAG1());
        manager.addPass(new DAG1Passes.D_DAG1());
        manager.addPass(new DAG1Passes.E_DAG1());
        manager.addPass(new DAG1Passes.F_DAG1());
        manager.addPass(new DAG1Passes.G_DAG1());
        manager.addPass(new DAG1Passes.H_DAG1());
        manager.addPass(new DAG1Passes.I_DAG1());

        program.indexed();
        manager.run(program);

        Assertions.assertEquals(3, manager.getCurrentPipeline().numberSteps());
        Assertions.assertEquals(9, manager.getCurrentPipeline().numberPasses());
    }
}
