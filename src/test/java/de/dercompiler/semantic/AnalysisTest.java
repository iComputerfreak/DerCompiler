package de.dercompiler.semantic;

import de.dercompiler.actions.CheckAction;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.LexerTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class AnalysisTest {


    @BeforeAll
    static void setup() {
        OutputMessageHandler.setDebug();
    }

    private static void checkFile(File file) {
        String filename = file.getName();
        // Skip output files for now (and any other files that are not test cases)

        System.out.println("Testing file " + filename);
        Source source = Source.forFile(file);
        CheckAction action = new CheckAction(source, true);

        // Tests that should succeed
        if (filename.endsWith(".invalid.mj")) {
            // Make sure that the test really fails
            boolean error = false;
            try {
                action.run();
                assertFalse(OutputMessageHandler.getEvents().isEmpty());
                error = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            assertTrue(error);
            OutputMessageHandler.clearDebugEvents();

        } else {
            try {
                action.run();
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
            assertTrue(OutputMessageHandler.getEvents().isEmpty());
        }
    }


    @BeforeEach
    void beforeTests() {
        OutputMessageHandler.clearDebugEvents();
    }

    @Test
    void testCases() {
        // Test the output for all files
        File[] files = getResourceFolderFiles("semantic", validInvalidFiles());
        Arrays.stream(files).forEach(AnalysisTest::checkFile);

        OutputMessageHandler.setTestOutput(true);
    }

    @Test
    void checkFeedback() {
        // Test the output for all files
        File[] files = getResourceFolderFiles("feedback", validInvalidFiles());
        Arrays.stream(files).forEach(AnalysisTest::checkFile);

        OutputMessageHandler.setTestOutput(true);
    }

    @Test
    void nullTest() {
        // Test the output for all files
        File[] files = getResourceFolderFiles("transformation", javaFiles());
        Arrays.stream(files).forEach(AnalysisTest::checkFile);

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

    private static FileFilter validInvalidFiles() {
        return file -> {
            String pathName = file.toString();
            return ((pathName.endsWith(".valid.mj") || pathName.endsWith(".invalid.mj")));
        };
    }

    private static FileFilter javaFiles() {
        return file -> {
            String path = file.toString();
            return path.endsWith(".java") || path.endsWith(".mj");
        };
    }

}
