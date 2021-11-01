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

import java.util.Arrays;
import java.util.Iterator;
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

    private void testSyntaxEqual(String expression, ASTNode created, ASTNode compare, Lexer lexer) {
        boolean equal = created.syntaxEqual(compare);
        if (!equal) {
            System.err.println("error: " + expression);
            created.syntaxEqual(compare);
        }
        assertTrue(equal);
        assertEquals(lexer.peek().type(), Token.EOF);
    }

    @Test
    @DisplayName("unary expression")
    void unary() {
        String[] unary = {
            "!foo",
            "!true",
            "-foo",
            "---4",
            "foo",
            "123"
        };
        ASTNode[] unary_expected = {
            new LogicalNotExpression(new Variable("foo")),
            new LogicalNotExpression(new BooleanValue(true)),
            new NegativeExpression(new Variable("foo")),
            new NegativeExpression(new NegativeExpression(new NegativeExpression(new IntegerValue("4")))),
            new Variable("foo"),
            new IntegerValue("123")
        };

        assert(unary.length == unary_expected.length);
        Iterator<String> lexValue = Arrays.stream(unary).iterator();
        Iterator<ASTNode> expected = Arrays.stream(unary_expected).iterator();
        while(lexValue.hasNext()) {
            String lexString = lexValue.next();
            Lexer lexer = Lexer.forString(lexString);
            Parser parser = new Parser(lexer);
            ASTNode created = parser.parseUnaryExpression();
            testSyntaxEqual(lexString, created, expected.next(), lexer);
        }
    }

    @Test
    @DisplayName("Posfix Expression")
    void posfix() {
        String[] posfix = {
                "foo.bar()",
                "foo.bar",
                //TODO add again
                "foo[3]"
        };
        ASTNode[] posfix_expected = new ASTNode[]{
                new MethodInvocationOnObject(new Variable("foo"), "bar", new Arguments()),
                new FieldAccess(new Variable("foo"), "bar"),
                new ArrayAccess(new Variable("foo"), new IntegerValue("3"))
        };

        assert(posfix.length == posfix_expected.length);
        Iterator<String> lexValue = Arrays.stream(posfix).iterator();
        Iterator<ASTNode> expected = Arrays.stream(posfix_expected).iterator();
        while(lexValue.hasNext()) {
            String lexString = lexValue.next();
            Lexer lexer = Lexer.forString(lexString);
            Parser parser = new Parser(lexer);
            ASTNode created = parser.parsePostfixExpression();
            testSyntaxEqual(lexString, created, expected.next(), lexer);
        }
    }

    @Test
    @DisplayName("Primary Expressions")
    void primary() {
        String[] primary = {"null", "false", "true", "0", "123", "var", "foo()", "foo(true, 3)", "this", "(5)", "new foo()", "new int[2]", "new int[2][][]"};
        ASTNode[] primary_expected = new ASTNode[]{
                new NullValue(),
                new BooleanValue(false),
                new BooleanValue(true),
                new IntegerValue("0"),
                new IntegerValue("123"),
                new Variable("var"),
                new MethodInvocationOnObject(new ThisValue(), "foo", new Arguments()),
                new MethodInvocationOnObject(new ThisValue(), "foo", new Arguments(Stream.of(new BooleanValue(true), new IntegerValue("3")).collect(Collectors.toList()))),
                new ThisValue(),
                new IntegerValue("5"),
                new NewObjectExpression(new CustomType("foo")),
                new NewArrayExpression(new IntType(), new IntegerValue("2"), 0),
                new NewArrayExpression(new IntType(), new IntegerValue("2"), 2)
        };

        assert(primary.length == primary_expected.length);
        Iterator<String> lexValue = Arrays.stream(primary).iterator();
        Iterator<ASTNode> expected = Arrays.stream(primary_expected).iterator();
        while(lexValue.hasNext()) {
            String lexString = lexValue.next();
            Lexer lexer = Lexer.forString(lexString);
            Parser parser = new Parser(lexer);
            ASTNode created = parser.parsePrimaryExpression();
            testSyntaxEqual(lexString, created, expected.next(), lexer);
        }
    }
}
