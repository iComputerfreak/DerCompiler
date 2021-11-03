package de.dercompiler.parser;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.BooleanType;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.IntType;
import de.dercompiler.ast.type.Type;
import de.dercompiler.io.OutputMessageHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;

public class ParserTestStatements {

    private static ParserTestHelper pth = new ParserTestHelper();

    @BeforeAll
    static void setup() {
        OutputMessageHandler.setDebug();
    }

    private void testLexstringEqualASTNode(String[] strings, ASTNode[] nodes) {
        pth.testLexstringEqualASTNode(strings, nodes, Parser::parseStatement);
    }

    @Test
    @DisplayName("local variable declaration")
    void testLocalVariableDeclaration() {
        String[] locals = {
                "int foo = 0;",
                "boolean bar = !true;",
                "foo bar = new foo();",
        };
        Type Int = new Type(new IntType(), null);
        Type bool = new Type(new BooleanType(), null);
        Variable foo = new Variable("foo");
        Type fooType = new Type(new CustomType(foo.getName()), null);
        Variable bar = new Variable("bar");
        ASTNode[] locals_expected = {
            new LocalVariableDeclarationStatement(Int, foo.getName(), new IntegerValue("0")),
            new LocalVariableDeclarationStatement(bool, bar.getName(), new LogicalNotExpression(new BooleanValue(true))),
            new LocalVariableDeclarationStatement(fooType, bar.getName(), new NewObjectExpression((CustomType) fooType.getBasicType()))
        };
        pth.testLexstringEqualASTNode(locals, locals_expected, Parser::parseVariableDeclaration);
    }

    @Test
    @DisplayName("block statement")
    void testBlockStatement() {
        String[] blocks = {
                "{}",
                "{foo = bar;}",
                "{int i = 0; while (i < 10) {i = i + 1;}}",
        };
        Variable i = new Variable("i");
        Variable foo = new Variable("foo");
        Variable bar = new Variable("bar");
        IntegerValue zero = new IntegerValue("0");
        ASTNode[] blocks_expected = {
                new BasicBlock(),
                new BasicBlock(new LinkedList<>(Arrays.asList(
                        new ExpressionStatement(new AssignmentExpression(foo, bar))
                ))),
                new BasicBlock(new LinkedList<>(Arrays.asList(
                        new LocalVariableDeclarationStatement(new Type(new IntType(), null), i.getName(), zero),
                        new WhileStatement(new LessExpression(i, new IntegerValue("10")), new BasicBlock(
                                new LinkedList<>(Arrays.asList(new ExpressionStatement(
                                    new AssignmentExpression(i, new AddExpression(i, new IntegerValue("1")))
                                )))
                        ))
               )))
        };
        testLexstringEqualASTNode(blocks, blocks_expected);
    }

    @Test
    @DisplayName("return expression")
    void testReturnStatement() {
        String[] returns = {
            "return;",
            "return foo;",
            "return foo + bar;"
        };
        Variable foo = new Variable("foo");
        Variable bar = new Variable("bar");
        ASTNode[] returns_expected = {
            new ReturnStatement(new VoidExpression()),
            new ReturnStatement(foo),
            new ReturnStatement(new AddExpression(foo, bar))
        };
        testLexstringEqualASTNode(returns, returns_expected);
    }

    @Test
    @DisplayName("while expression")
    void testWhileStatement() {
        String[] whiles = {
                "while(true);",
                "while(true)while(true);",
        };
        AbstractExpression cond = new BooleanValue(true);
        Statement empty = new EmptyStatement();
        ASTNode[] whiles_expected = {
            new WhileStatement(cond, empty),
            new WhileStatement(cond, new WhileStatement(cond, empty))
        };
        testLexstringEqualASTNode(whiles, whiles_expected);
    }

    @Test
    @DisplayName("expression statement")
    void testExpressionStatement() {
        String[] expr = {
            "a;",
            "foo + foo;",
            "foo.bar();",
                //TODO add, but doesn't work currently because of a lexer bug
            //"a = b[i] + c.d;",
        };
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        Variable d = new Variable("d");
        Variable i = new Variable("i");
        Variable foo = new Variable("foo");
        Variable bar = new Variable("bar");
        ASTNode[] expr_expected = {
            new ExpressionStatement(a),
            new ExpressionStatement(new AddExpression(foo, foo)),
            new ExpressionStatement(new MethodInvocationOnObject(foo, bar.getName(), new Arguments())),
          /*  new ExpressionStatement(new AssignmentExpression(a, new AddExpression(
                    new ArrayAccess(b, i),
                    new FieldAccess(c, d.getName())
            ))),*/
        };
        testLexstringEqualASTNode(expr, expr_expected);
    }

    @Test
    @DisplayName("if statement")
    void testIfStatement() {
        String[] ifs = {
            "if (true);",
            "if (true); else ;",
            "if (true) if (true);",
            "if (true) if (true); else;",
        };
        AbstractExpression cond = new BooleanValue(true);
        Statement empty = new EmptyStatement();
        ASTNode[] ifs_expected = {
            new IfStatement(cond, empty, null),
            new IfStatement(cond, empty, empty),
            new IfStatement(cond, new IfStatement(cond, empty, null), null),
            new IfStatement(cond, new IfStatement(cond, empty, empty), null),
        };
        testLexstringEqualASTNode(ifs, ifs_expected);
    }

    @Test
    @DisplayName("empty statement")
    void testEmptyStatement() {
        String[] empty = {";"};
        ASTNode[] empty_expected = {new EmptyStatement()};
        testLexstringEqualASTNode(empty, empty_expected);
    }
}
