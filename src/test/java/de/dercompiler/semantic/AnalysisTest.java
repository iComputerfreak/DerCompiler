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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnalysisTest {


    @BeforeAll
    static void setup() {
        OutputMessageHandler.setDebug();
    }

    @BeforeEach
    void beforeTests() {
        OutputMessageHandler.clearDebugEvents();
    }

    @Test
    void testCases() {
        //OutputMessageHandler.setTestOutput(false);
        // Test the output for all files
        File[] files = getResourceFolderFiles("semantic");
        Iterator<File> iterator = Arrays.stream(files).iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            String filename = file.getName();
            // Skip output files for now (and any other files that are not test cases)

            System.out.println("Testing file " + filename);
            Source source = Source.forFile(file);
            CheckAction action = new CheckAction(source, true);

            // Tests that should succeed
            if (filename.endsWith(".valid.mj")) {
                try {
                    action.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    assertTrue(false);
                }
                assertTrue(OutputMessageHandler.getEvents().isEmpty());
            } else if (filename.endsWith(".invalid.mj")) {
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

            }
        }
        OutputMessageHandler.setTestOutput(true);
    }

    private static File[] getResourceFolderFiles(String folder) {
        try {
            ClassLoader loader = LexerTest.class.getClassLoader();
            URI uri = loader.getResource(folder).toURI();
            String path = uri.getPath();
            return new File(path).listFiles((file -> {
                String pathName = file.toString();
                return ((pathName.endsWith(".valid.mj") || pathName.endsWith(".invalid.mj")));
            }));
        } catch (URISyntaxException e) {
            new OutputMessageHandler(MessageOrigin.TEST).internalError("Error converting test file path to URI");
            return new File[]{};
        }
    }

}
