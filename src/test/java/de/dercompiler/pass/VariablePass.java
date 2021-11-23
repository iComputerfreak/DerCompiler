package de.dercompiler.pass;

import de.dercompiler.actions.CheckAction;
import de.dercompiler.ast.Program;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.parser.Parser;
import de.dercompiler.pass.passes.*;
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
        Source source = Source.forString(testProgram);
        new CheckAction(source, false).run();
    }
}
