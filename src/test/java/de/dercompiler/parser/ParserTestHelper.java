package de.dercompiler.parser;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.Token;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTestHelper {

    public static SourcePosition DEFAULT_POS = Lexer.forString("").getPosition();

    private void testSyntaxEqual(String expression, ASTNode created, ASTNode compare, Lexer lexer) {
        boolean equal = created.syntaxEquals(compare);
        if (!equal) {
            System.err.println("error: " + expression);
            created.syntaxEquals(compare);
        }
        assertTrue(equal);
        assertEquals(lexer.peek().type(), Token.EOF);
    }

    public interface ParserFunction {
        public ASTNode parse(Parser parser);
    }

    public void testLexstringEqualASTNode(String[] strings, ASTNode[] nodes, ParserFunction func) {
        assert(strings.length == nodes.length);
        Iterator<String> lexValue = Arrays.stream(strings).iterator();
        Iterator<ASTNode> expected = Arrays.stream(nodes).iterator();
        while(lexValue.hasNext()) {
            String lexString = lexValue.next();
            Lexer lexer = Lexer.forString(lexString);
            Parser parser = new Parser(lexer);
            ASTNode created = func.parse(parser);
            testSyntaxEqual(lexString, created, expected.next(), lexer);
        }
    }
}
