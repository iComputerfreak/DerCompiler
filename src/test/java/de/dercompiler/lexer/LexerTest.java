package de.dercompiler.lexer;

import static org.junit.jupiter.api.Assertions.*;

import de.dercompiler.lexer.token.IToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.util.Iterator;

public class LexerTest {
    
    @BeforeAll
    static void setup() {
        // Runs before each test
    }
    
    @Test
    void testCases() {
        try {
            // Test the output for all files
            for (File file : getResourceFolderFiles("lexer")) {
                String filename = file.getName();
                // Skip output files for now (and any other files that are not test cases)
                if (!filename.endsWith(".mj")) {
                    continue;
                }
                // Tests that should succeed
                if (filename.endsWith(".valid.mj")) {
                    System.out.println("Testing file " + filename);
                    Lexer l = Lexer.forFile(new File(file.getPath()));
                    // Look for the output file
                    URL outputFile = this.getClass().getClassLoader().getResource("lexer/" + filename + ".out");
                    BufferedReader reader = new BufferedReader(new FileReader(outputFile.getPath()));
                    
                    String line = null;
                    int lineNr = 1;
                    while ((line = reader.readLine()) != null) {
                        // The next lexer token has to match the output line
                        IToken t = l.nextToken().type();
                        assertEquals(line, t.toString(), "in line " + lineNr);
                        lineNr += 1;
                    }
                }
                // TODO: Test .invalid.mj files
                // We currently can't test them as unit tests, since they immediately exit the program on error
                // Maybe we need to add them to the tests.sh and check for a non-zero exit code
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static File[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();
    }
}