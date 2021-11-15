package de.dercompiler.pass;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.passes.TestPass;
import org.junit.jupiter.api.*;

import javax.print.attribute.standard.DocumentName;

import static org.junit.jupiter.api.Assertions.*;

public class EasyTest {

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

    }

    @AfterEach
    void cleanup() {

    }

    @AfterAll
    static void clean() {

    }

    @Test
    @DisplayName("run check 1")
    void runCheck1() {
        Program program = new Parser(Lexer.forString(testProgram)).parseProgram();


        PassManager manager = new PassManager();



        manager.addPass(generateTestPass(new AnalysisUsage()));

        manager.run(program);

    }

    public static Pass generateTestPass(AnalysisUsage usage) {
        return new TestPass() {
            private long id = 0;

            private AnalysisUsage use = usage;
            private PassManager manager;



            @Override
            public void doInitialization(Program program) {

            }

            @Override
            public void doFinalization(Program program) {

            }

            @Override
            public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {

                return usage;
            }

            @Override
            public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
                return usage;
            }

            @Override
            public void registerPassManager(PassManager manager) {
                this.manager = manager;
            }

            @Override
            public PassManager getPassManager() {
                return manager;
            }

            @Override
            public long registerID(long id) {
                if (this.id != 0) {
                    return (this.id = id);
                }
                return this.id;
            }

            @Override
            public long getID() {
                return id;
            }


        };
    }
}
