package de.dercompiler.parser;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.IntType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.SourcePosition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParserTestExpressions {

    public static SourcePosition DEFAULT_POS = Lexer.forString("").peek().position();

    private ParserTestHelper pth = new ParserTestHelper();

    @BeforeAll
    static void setup() {
        OutputMessageHandler.setDebug();
    }

    @BeforeEach
    void beforeTests() {
        OutputMessageHandler.clearDebugEvents();
    }

    private void testLexstringEqualASTNode(String[] strings, ASTNode[] nodes, ParserTestHelper.ParserFunction func) {
        pth.testLexstringEqualASTNode(strings, nodes, func);
    }

    @Test
    @DisplayName("precedence complicated")
    void precedence_complicated() {
        String[] pc = {
                "foo = bar + baz",
                "foo = bar + baz * foo",
                "foo = foo % 5 == 0 || bar != baz && baz * baz / baz <= baz",
                "foo = bar = baz"
        };
        Variable foo = new Variable(DEFAULT_POS,"foo");
        Variable bar = new Variable(DEFAULT_POS,"bar");
        Variable baz = new Variable(DEFAULT_POS,"baz");

        ASTNode[] pc_expected = {
                new AssignmentExpression(DEFAULT_POS,foo, new AddExpression(DEFAULT_POS, bar, baz)),
                new AssignmentExpression(DEFAULT_POS,foo, new AddExpression(DEFAULT_POS,bar, new MultiplyExpression(DEFAULT_POS, baz, foo))),
                new AssignmentExpression(DEFAULT_POS,foo, new LogicalOrExpression(DEFAULT_POS,
                        new EqualExpression(DEFAULT_POS, new ModuloExpression(DEFAULT_POS,foo, new IntegerValue(DEFAULT_POS,"5")), new IntegerValue(DEFAULT_POS,"0")),
                        new LogicalAndExpression(DEFAULT_POS,
                                new NotEqualExpression(DEFAULT_POS,bar, baz),
                                new LessEqualExpression(DEFAULT_POS,
                                    new DivisionExpression(DEFAULT_POS,new MultiplyExpression(DEFAULT_POS, baz, baz), baz),
                                    baz
                                )
                        )
                )),
                new AssignmentExpression(DEFAULT_POS, foo, new AssignmentExpression(DEFAULT_POS, bar, baz)),
        };
        testLexstringEqualASTNode(pc, pc_expected, Parser::parseExpression);
    }

    @Test
    @DisplayName("precedence")
    void precedence() {
        String[] precedence = {
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
        Variable foo = new Variable(DEFAULT_POS, "foo");
        Variable bar = new Variable(DEFAULT_POS, "bar");
        ASTNode[] precedence_expected = {
                new AssignmentExpression(DEFAULT_POS, foo, bar),
                new LogicalOrExpression(DEFAULT_POS, foo, bar),
                new LogicalAndExpression(DEFAULT_POS, foo, bar),
                new EqualExpression(DEFAULT_POS, foo, bar),
                new NotEqualExpression(DEFAULT_POS, foo, bar),
                new LessExpression(DEFAULT_POS, foo, bar),
                new LessEqualExpression(DEFAULT_POS, foo, bar),
                new GreaterExpression(DEFAULT_POS, foo, bar),
                new GreaterEqualExpression(DEFAULT_POS, foo, bar),
                new AddExpression(DEFAULT_POS, foo, bar),
                new SubtractExpression(DEFAULT_POS, foo, bar),
                new MultiplyExpression(DEFAULT_POS, foo, bar),
                new DivisionExpression(DEFAULT_POS, foo, bar),
                new ModuloExpression(DEFAULT_POS, foo, bar)
        };
        testLexstringEqualASTNode(precedence, precedence_expected, Parser::parseExpression);
    }

    @Test
    @DisplayName("unary expression")
    void unary() {
        String[] unary = {
            "!foo",
            "!true",
            "!!true",
            "-foo",
            "foo",
            "123"
        };
        ASTNode[] unary_expected = {
            new LogicalNotExpression(DEFAULT_POS, new Variable(DEFAULT_POS, "foo")),
            new LogicalNotExpression(DEFAULT_POS, new BooleanValue(DEFAULT_POS, true)),
            new LogicalNotExpression(DEFAULT_POS, new LogicalNotExpression(DEFAULT_POS, new BooleanValue(DEFAULT_POS, true))),
            new NegativeExpression(DEFAULT_POS, new Variable(DEFAULT_POS, "foo")),
            new Variable(DEFAULT_POS, "foo"),
            new IntegerValue(DEFAULT_POS, "123")
        };

        testLexstringEqualASTNode(unary, unary_expected, Parser::parseUnaryExpression);
    }

    @Test
    @DisplayName("Postfix Expression")
    void postfix() {
        String[] postfix = {
                "foo.bar()",
                "foo.bar",
                "foo[3]"
        };
        ASTNode[] posfix_expected = new ASTNode[]{
                new MethodInvocationOnObject(DEFAULT_POS, new Variable(DEFAULT_POS, "foo"), "bar", new Arguments(DEFAULT_POS)),
                new FieldAccess(DEFAULT_POS, new Variable(DEFAULT_POS, "foo"), "bar"),
                new ArrayAccess(DEFAULT_POS, new Variable(DEFAULT_POS, "foo"), new IntegerValue(DEFAULT_POS, "3"))
        };

        testLexstringEqualASTNode(postfix, posfix_expected, Parser::parsePostfixExpression);
    }

    @Test
    @DisplayName("Primary Expressions")
    void primary() {
        String[] primary = {"null", "false", "true", "0", "123", "var", "foo()", "foo(true, 3)", "this", "(5)", "new foo()", "new int[2]", "new int[2][][]"};
        ASTNode[] primary_expected = new ASTNode[]{
                new NullValue(DEFAULT_POS),
                new BooleanValue(DEFAULT_POS, false),
                new BooleanValue(DEFAULT_POS, true),
                new IntegerValue(DEFAULT_POS, "0"),
                new IntegerValue(DEFAULT_POS, "123"),
                new Variable(DEFAULT_POS, "var"),
                new MethodInvocationOnObject(DEFAULT_POS, new ThisValue(DEFAULT_POS), "foo", new Arguments(DEFAULT_POS)),
                new MethodInvocationOnObject(DEFAULT_POS, new ThisValue(DEFAULT_POS), "foo", new Arguments(DEFAULT_POS, Stream.of(new BooleanValue(DEFAULT_POS, true), new IntegerValue(DEFAULT_POS, "3")).collect(Collectors.toList()))),
                new ThisValue(DEFAULT_POS),
                new IntegerValue(DEFAULT_POS, "5"),
                new NewObjectExpression(DEFAULT_POS, new CustomType(DEFAULT_POS,  "foo")),
                new NewArrayExpression(DEFAULT_POS, new IntType(DEFAULT_POS), new IntegerValue(DEFAULT_POS, "2"), 1),
                new NewArrayExpression(DEFAULT_POS, new IntType(DEFAULT_POS), new IntegerValue(DEFAULT_POS, "2"), 3)
        };

        testLexstringEqualASTNode(primary, primary_expected, Parser::parsePrimaryExpression);
    }
}
