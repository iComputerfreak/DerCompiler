package de.dercompiler.parser;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.IntType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.token.Token;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.text.html.HTMLDocument;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTestUnaryExpressions {

    @BeforeAll
    static void setup() {
        OutputMessageHandler.setDebug();
    }

    @BeforeEach
    void beforeTests() {
        OutputMessageHandler.clearDebugEvents();
    }

    private void testSyntaxEqual(ASTNode created, ASTNode compare, Lexer lexer) {
        assertTrue(created.syntaxEqual(compare));
        assertEquals(lexer.peek().type(), Token.EOF);
    }

    @Test
    @DisplayName("Primary Expressions")
    void parsePrimary() {
        String[] primary = {"null", "false", "true", "0", "123", "var", "foo()"/*, "foo(true, 3)"*/, "this", /*"(5)",*/ "new foo()", /*"new int[2]", "new int[2][][]"*/};
        ASTNode[] primary_expected = new ASTNode[]{
                new NullValue(),
                new BooleanValue(false),
                new BooleanValue(true),
                new IntegerValue("0"),
                new IntegerValue("123"),
                new Variable("var"),
                new MethodInvocationOnObject(new ThisValue(), "foo", new Arguments()),
                /*TODO add*/
                //new MethodInvocationOnObject(new ThisValue(), "foo", new Arguments(Stream.of(new BooleanValue(true), new IntegerValue("3")).collect(Collectors.toList()))),
                new ThisValue(),
                //new IntegerValue("5"),
                new NewObjectExpression(new CustomType("foo")),
                //new NewArrayExpression(new IntType(), new IntegerValue("2"), 0),
                //new NewArrayExpression(new IntType(), new IntegerValue("2"), 2)
        };

        assert(primary.length == primary_expected.length);
        Iterator<String> lexValue = Arrays.stream(primary).iterator();
        Iterator<ASTNode> expected = Arrays.stream(primary_expected).iterator();
        while(lexValue.hasNext()) {
            Lexer lexer = Lexer.forString(lexValue.next());
            Parser parser = new Parser(lexer);
            ASTNode created = parser.parsePrimaryExpression();
            testSyntaxEqual(created, expected.next(), lexer);
        }
    }
}
