package de.dercompiler.lexer;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.token.Token;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {

    @BeforeAll
    static void setup() {
        // Runs before each test
        OutputMessageHandler.setDebug();
        OutputMessageHandler.setTestOutput(false);
    }

    @AfterAll
    static void cleanUp() {
        OutputMessageHandler.setTestOutput(true);
    }

    @Test
    void testCases() {
        try {
            // Test the output for all files
            for (File file : getResourceFolderFiles("lexer")) {
                String filename = file.getName();
                // Skip output files for now (and any other files that are not test cases)

                System.out.println("Testing file " + filename);
                Lexer l = TestLexer.forFile(new File(file.getPath()));
                // Tests that should succeed
                if (filename.endsWith(".valid.mj")) {
                    // Look for the output file
                    URL resource = this.getClass().getClassLoader().getResource("lexer/" + filename + ".out");
                    if (Objects.isNull(resource)) {
                        fail("LexerTest %s is missing its output file! Better supply it quickly!");
                    }
                    URI outputFile = resource.toURI();
                    BufferedReader reader = new BufferedReader(new FileReader(outputFile.getPath()));

                    String line;
                    int lineNr = 1;
                    while ((line = reader.readLine()) != null) {
                        // The next lexer token has to match the output line
                        IToken t = l.nextToken().type();
                        assertEquals(line, t.toString(), "in line " + lineNr);
                        lineNr += 1;
                    }
                }

                else if (filename.endsWith(".invalid.mj")) {
                    // Make sure that the test really fails
                    assertThrows(TestLexer.LexerException.class, () -> {
                        TokenOccurrence token;
                        do {
                            token = l.nextToken();
                        } while (token.type() != Token.EOF);
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            new OutputMessageHandler(MessageOrigin.TEST).internalError("Error converting test file path to URI");
        }
    }

    private static File[] getResourceFolderFiles(String folder) {
        try {
            ClassLoader loader = LexerTest.class.getClassLoader();
            URI uri = loader.getResource(folder).toURI();
            String path = uri.getPath();
            return new File(path).listFiles((file -> {
                String pathName = file.toString();
                return pathName.endsWith(".valid.mj") || pathName.endsWith(".invalid.mj");
            }));
        } catch (URISyntaxException e) {
            new OutputMessageHandler(MessageOrigin.TEST).internalError("Error converting test file path to URI");
            return new File[]{};
        }
    }
}