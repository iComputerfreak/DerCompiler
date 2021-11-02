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

public class ParserTestExpressions {

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

    private interface ParserFunction {
        public ASTNode parse(Parser parser);
    }

    private void testLexstringEqualASTNode(String[] strings, ASTNode[] nodes, ParserFunction func) {
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

    @Test
    @DisplayName("precidence complicated")
    void precidence_complicated() {
        String[] pc = {
                "foo = bar + baz",
                "foo = bar + baz * foo",
                "foo = foo % 5 == 0 || bar != baz && baz * baz / baz <= baz"
        };
        Variable foo = new Variable("foo");
        Variable bar = new Variable("bar");
        Variable baz = new Variable("baz");

        ASTNode[] pc_expected = {
                new AssignmentExpression(foo, new AddExpression(bar, baz)),
                new AssignmentExpression(foo, new AddExpression(bar, new MultiplyExpression(baz, foo))),
                new AssignmentExpression(foo, new LogicalOrExpression(
                        new EqualExpression(new ModuloExpression(foo, new IntegerValue("5")), new IntegerValue("0")),
                        new LogicalAndExpression(
                                new NotEqualExpression(bar, baz),
                                new LessEqualExpression(
                                    new DivisionExpression(new MultiplyExpression(baz, baz), baz),
                                    baz
                                )
                        )
                )),
        };
        testLexstringEqualASTNode(pc, pc_expected, Parser::parseExpression);
    }

    @Test
    @DisplayName("precidence")
    void precidence() {
        String[] precidence = {
                "foo = bar",
                "foo || bar",
                "foo && bar",
                "foo == bar",
                "foo != bar",
                "foo < bar",
                "foo <= bar",
                "foo > bar",
                "foo >= bar",
                "foo + bar",
                "foo - bar",
                "foo * bar",
                "foo / bar",
                "foo % bar",
        };
        Variable foo = new Variable("foo");
        Variable bar = new Variable("bar");
        ASTNode[] precidence_expected = {
                new AssignmentExpression(foo, bar),
                new LogicalOrExpression(foo, bar),
                new LogicalAndExpression(foo, bar),
                new EqualExpression(foo, bar),
                new NotEqualExpression(foo, bar),
                new LessExpression(foo, bar),
                new LessEqualExpression(foo, bar),
                new GreaterExpression(foo, bar),
                new GreaterEqualExpression(foo, bar),
                new AddExpression(foo, bar),
                new SubtractExpression(foo, bar),
                new MultiplyExpression(foo, bar),
                new DivisionExpression(foo, bar),
                new ModuloExpression(foo, bar)
        };
        testLexstringEqualASTNode(precidence, precidence_expected, Parser::parseExpression);
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

        testLexstringEqualASTNode(unary, unary_expected, Parser::parseUnaryExpression);
    }

    @Test
    @DisplayName("Posfix Expression")
    void posfix() {
        String[] posfix = {
                "foo.bar()",
                "foo.bar",
                "foo[3]"
        };
        ASTNode[] posfix_expected = new ASTNode[]{
                new MethodInvocationOnObject(new Variable("foo"), "bar", new Arguments()),
                new FieldAccess(new Variable("foo"), "bar"),
                new ArrayAccess(new Variable("foo"), new IntegerValue("3"))
        };

        testLexstringEqualASTNode(posfix, posfix_expected, Parser::parsePostfixExpression);
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

        testLexstringEqualASTNode(primary, primary_expected, Parser::parsePrimaryExpression);
    }
}
