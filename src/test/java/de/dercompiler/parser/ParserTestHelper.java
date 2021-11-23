package de.dercompiler.parser;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.LexerTest;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.Token;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTestHelper {

    public static SourcePosition DEFAULT_POS = Lexer.forString("").peek().position();

    private static void testSyntaxEqual(String expression, ASTNode created, ASTNode compare, Lexer lexer) {
        boolean equal = created.syntaxEquals(compare);
        if (!equal) {
            System.err.println("error: " + expression);
            created.syntaxEquals(compare);
        }
        assertTrue(equal);
        assertEquals(lexer.peek().type(), Token.EOF);
    }

    public interface ParserFunction {
        ASTNode parse(Parser parser, AnchorSet ank);
    }

    public static void testLexstringEqualASTNode(String[] strings, ASTNode[] nodes, ParserFunction func) {
        assert(strings.length == nodes.length);
        Iterator<String> lexValue = Arrays.stream(strings).iterator();
        Iterator<ASTNode> expected = Arrays.stream(nodes).iterator();
        while(lexValue.hasNext()) {
            String lexString = lexValue.next();
            Lexer lexer = Lexer.forString(lexString);
            Parser parser = new Parser(lexer);
            ASTNode created = func.parse(parser, new AnchorSet());
            testSyntaxEqual(lexString, created, expected.next(), lexer);
        }
    }

    static File[] getResourceFolderFiles(String folder) {
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

    static File getLineSweep() {
        File[] files = getResourceFolderFiles("parser");
        for (File file : files) {
            if (file.getName().equals("Linesweep.valid.mj")) return file;
        }
        return null;
    }
}
