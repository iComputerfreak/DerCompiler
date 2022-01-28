package de.dercompiler.intermediate;

import de.dercompiler.actions.CompileAction;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.LexerTest;
import de.dercompiler.transformation.FirmSetup;
import firm.Firm;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class IntermediateTest {


    @BeforeAll
    static void setup() {
        FirmSetup.firmSetupForTests();
        Firm.init(null, new String[]{ "pic=1" });
        System.out.println("Initialized libFirm Version: " + Firm.getMinorVersion() + "." + Firm.getMajorVersion());
        OutputMessageHandler.setDebug();
    }

    @AfterAll
    static void cleanUp() {
        Firm.finish();
    }

    private static void checkFile(File file) {
        String filename = file.getName();
        // Skip output files for now (and any other files that are not test cases)

        System.out.println("Testing file " + filename);
        Source source = Source.forFile(file);
        CompileAction action = new CompileAction(source);

        // All tests should complete transformation into IR graphs
        try {
            action.run();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertTrue(OutputMessageHandler.getEvents().isEmpty());

    }


    @BeforeEach
    void beforeTests() {
        OutputMessageHandler.clearDebugEvents();
    }

    @Test
    void testCases() {
        // Test the output for all files
        File[] files = getResourceFolderFiles("transformation", javaFiles());
        Arrays.stream(files).forEach(IntermediateTest::checkFile);

        OutputMessageHandler.setTestOutput(true);
    }

    private static File[] getResourceFolderFiles(String folder, FileFilter filter) {
        try {
            ClassLoader loader = LexerTest.class.getClassLoader();
            URI uri = loader.getResource(folder).toURI();
            String path = uri.getPath();
            return new File(path).listFiles(filter);
        } catch (URISyntaxException e) {
            new OutputMessageHandler(MessageOrigin.TEST).internalError("Error converting test file path to URI");
            return new File[]{};
        }
    }

    private static FileFilter javaFiles() {
        return file -> {
            String path = file.toString();
            return path.endsWith(".java") || path.endsWith(".mj");
        };
    }

}
