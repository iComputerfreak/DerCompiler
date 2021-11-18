package de.dercompiler.pass;

import de.dercompiler.ast.Program;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.passes.EnterScopePass;
import de.dercompiler.pass.passes.InterClassAnalysisCheckPass;
import de.dercompiler.pass.passes.LeaveScopePass;
import de.dercompiler.pass.passes.VariableAnalysisCheckPass;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VariablePass {
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
        PassManager.setPrintPipeline(false);
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

        manager.addPass(new InterClassAnalysisCheckPass());
        manager.addPass(new EnterScopePass());
        manager.addPass(new VariableAnalysisCheckPass());
        manager.addPass(new LeaveScopePass());

        manager.run(program);


    }
}
