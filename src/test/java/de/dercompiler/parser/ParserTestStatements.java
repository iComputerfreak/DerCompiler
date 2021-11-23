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

import static de.dercompiler.parser.ParserTestHelper.DEFAULT_POS;

public class ParserTestStatements {

    private static final ParserTestHelper pth = new ParserTestHelper();

    @BeforeAll
    static void setup() {
        OutputMessageHandler.setDebug();
    }

    private void testLexstringEqualASTNode(String[] strings, ASTNode[] nodes) {
        ParserTestHelper.testLexstringEqualASTNode(strings, nodes, Parser::parseStatement);
    }

    @Test
    @DisplayName("local variable declaration")
    void testLocalVariableDeclaration() {
        String[] locals = {
                "int foo = 0;",
                "boolean bar = !true;",
                "foo bar = new foo();",
        };
        Type Int = new Type(DEFAULT_POS, new IntType(DEFAULT_POS), 0);
        Type bool = new Type(DEFAULT_POS, new BooleanType(DEFAULT_POS), 0);
        Variable foo = new Variable(DEFAULT_POS, "foo");
        Type fooType = new Type(DEFAULT_POS, new CustomType(DEFAULT_POS, foo.getName()), 0);
        Variable bar = new Variable(DEFAULT_POS, "bar");
        ASTNode[] locals_expected = {
            new LocalVariableDeclarationStatement(DEFAULT_POS, Int, foo.getName(), new IntegerValue(DEFAULT_POS, "0")),
            new LocalVariableDeclarationStatement(DEFAULT_POS, bool, bar.getName(), new LogicalNotExpression(DEFAULT_POS, new BooleanValue(DEFAULT_POS, true))),
            new LocalVariableDeclarationStatement(DEFAULT_POS, fooType, bar.getName(), new NewObjectExpression(DEFAULT_POS, (CustomType) fooType.getBasicType()))
        };
        ParserTestHelper.testLexstringEqualASTNode(locals, locals_expected, Parser::parseVariableDeclaration);
    }

    @Test
    @DisplayName("parse random code of the Parser")
    void testRandomCodesniped() {
        String[] code = {
              """
              {
                  Type type = parseType();
                  IdentifierToken ident = expectIdentifier();
                  AbstractExpression expression = new UninitializedValue();
                  if (wlexer.peek() == ASSIGN) {
                      expect(ASSIGN);
                      expression = parseExpression();
                      expect(SEMICOLON);
                  } else {
                      expect(SEMICOLON);
                  }
                  return createLocalVariableDeclarationStatement(type, ident.getIdentifier(), expression);
              }
              """
        };
        Variable Type = new Variable(DEFAULT_POS, "Type");
        Variable type = new Variable(DEFAULT_POS, "type");
        Variable parseType = new Variable(DEFAULT_POS, "parseType");
        Variable IdentifierToken = new Variable(DEFAULT_POS, "IdentifierToken");
        Variable expectIdentifier = new Variable(DEFAULT_POS, "expectIdentifier");
        Variable ident = new Variable(DEFAULT_POS, "ident");
        Variable AbstractExpression = new Variable(DEFAULT_POS, "AbstractExpression");
        Variable expression = new Variable(DEFAULT_POS, "expression");
        Variable UninitalizedValue = new Variable(DEFAULT_POS, "UninitializedValue");
        Variable wlexer = new Variable(DEFAULT_POS, "wlexer");
        Variable peek = new Variable(DEFAULT_POS, "peek");
        Variable expect = new Variable(DEFAULT_POS, "expect");
        Variable parseExpression = new Variable(DEFAULT_POS, "parseExpression");
        Variable assign = new Variable(DEFAULT_POS, "ASSIGN");
        Variable semicolon = new Variable(DEFAULT_POS, "SEMICOLON");
        Variable createLocalVariableDeclarationStatement = new Variable(DEFAULT_POS, "createLocalVariableDeclarationStatement");
        Variable getIdentifier = new Variable(DEFAULT_POS, "getIdentifier");
        ASTNode[] code_expected = {
            new BasicBlock(DEFAULT_POS, Arrays.asList(
                    new LocalVariableDeclarationStatement(DEFAULT_POS, new Type(DEFAULT_POS, new CustomType(DEFAULT_POS, Type.getName()), 0), type.getName(), new MethodInvocationOnObject(DEFAULT_POS, new ThisValue(DEFAULT_POS), parseType.getName(), new Arguments(DEFAULT_POS))),
                    new LocalVariableDeclarationStatement(DEFAULT_POS, new Type(DEFAULT_POS, new CustomType(DEFAULT_POS, IdentifierToken.getName()), 0), ident.getName(),new MethodInvocationOnObject(DEFAULT_POS, new ThisValue(DEFAULT_POS), expectIdentifier.getName(), new Arguments(DEFAULT_POS))),
                    new LocalVariableDeclarationStatement(DEFAULT_POS, new Type(DEFAULT_POS, new CustomType(DEFAULT_POS, AbstractExpression.getName()), 0), expression.getName(), new NewObjectExpression(DEFAULT_POS, new CustomType(DEFAULT_POS, UninitalizedValue.getName()))),
                    new IfStatement(DEFAULT_POS,
                            new EqualExpression(DEFAULT_POS, new MethodInvocationOnObject(DEFAULT_POS, wlexer, peek.getName(), new Arguments(DEFAULT_POS)), assign),
                            new BasicBlock(DEFAULT_POS, Arrays.asList(
                                    new ExpressionStatement(DEFAULT_POS, new MethodInvocationOnObject(DEFAULT_POS, new ThisValue(DEFAULT_POS), expect.getName(), new Arguments(DEFAULT_POS, Arrays.asList(assign)))),
                                    new ExpressionStatement(DEFAULT_POS, new AssignmentExpression(DEFAULT_POS, expression, new MethodInvocationOnObject(DEFAULT_POS, new ThisValue(DEFAULT_POS), parseExpression.getName(), new Arguments(DEFAULT_POS)))),
                                    new ExpressionStatement(DEFAULT_POS, new MethodInvocationOnObject(DEFAULT_POS, new ThisValue(DEFAULT_POS), expect.getName(), new Arguments(DEFAULT_POS, Arrays.asList(semicolon))))
                            )),
                            new BasicBlock(DEFAULT_POS, Arrays.asList(
                                    new ExpressionStatement(DEFAULT_POS, new MethodInvocationOnObject(DEFAULT_POS, new ThisValue(DEFAULT_POS), expect.getName(), new Arguments(DEFAULT_POS, Arrays.asList(semicolon))))
                            ))
                    ),
                    new ReturnStatement(DEFAULT_POS, new MethodInvocationOnObject(DEFAULT_POS, new ThisValue(DEFAULT_POS), createLocalVariableDeclarationStatement.getName(), new Arguments(DEFAULT_POS, Arrays.asList(
                            type,
                            new MethodInvocationOnObject(DEFAULT_POS, ident, getIdentifier.getName(), new Arguments(DEFAULT_POS)),
                            expression
                        )))
                    )
            ))
        };
        testLexstringEqualASTNode(code, code_expected);
    }

    @Test
    @DisplayName("block statement")
    void testBlockStatement() {
        String[] blocks = {
                "{}",
                "{foo = bar;}",
                "{int i = 0; while (i < 10) {i = i + 1;}}",
        };
        Variable i = new Variable(DEFAULT_POS, "i");
        Variable foo = new Variable(DEFAULT_POS, "foo");
        Variable bar = new Variable(DEFAULT_POS, "bar");
        IntegerValue zero = new IntegerValue(DEFAULT_POS, "0");
        ASTNode[] blocks_expected = {
                new BasicBlock(DEFAULT_POS),
                new BasicBlock(DEFAULT_POS, new LinkedList<>(Arrays.asList(
                        new ExpressionStatement(DEFAULT_POS, new AssignmentExpression(DEFAULT_POS, foo, bar))
                ))),
                new BasicBlock(DEFAULT_POS, new LinkedList<>(Arrays.asList(
                        new LocalVariableDeclarationStatement(DEFAULT_POS, new Type(DEFAULT_POS, new IntType(DEFAULT_POS), 0), i.getName(), zero),
                        new WhileStatement(DEFAULT_POS, new LessExpression(DEFAULT_POS, i, new IntegerValue(DEFAULT_POS, "10")), new BasicBlock(DEFAULT_POS,
                                new LinkedList<>(Arrays.asList( new ExpressionStatement(DEFAULT_POS,
                                        new AssignmentExpression(DEFAULT_POS, i, new AddExpression(DEFAULT_POS, i, new IntegerValue(DEFAULT_POS, "1")))
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
        Variable foo = new Variable(DEFAULT_POS, "foo");
        Variable bar = new Variable(DEFAULT_POS, "bar");
        ASTNode[] returns_expected = {
            new ReturnStatement(DEFAULT_POS, new VoidExpression(DEFAULT_POS)),
            new ReturnStatement(DEFAULT_POS, foo),
            new ReturnStatement(DEFAULT_POS, new AddExpression(DEFAULT_POS, foo, bar))
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
        Expression cond = new BooleanValue(DEFAULT_POS, true);
        Statement empty = new EmptyStatement(DEFAULT_POS);
        ASTNode[] whiles_expected = {
            new WhileStatement(DEFAULT_POS, cond, empty),
            new WhileStatement(DEFAULT_POS, cond, new WhileStatement(DEFAULT_POS, cond, empty))
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
            "a = b[i] + c.d;",
        };
        Variable a = new Variable(DEFAULT_POS, "a");
        Variable b = new Variable(DEFAULT_POS, "b");
        Variable c = new Variable(DEFAULT_POS, "c");
        Variable d = new Variable(DEFAULT_POS, "d");
        Variable i = new Variable(DEFAULT_POS, "i");
        Variable foo = new Variable(DEFAULT_POS, "foo");
        Variable bar = new Variable(DEFAULT_POS, "bar");
        ASTNode[] expr_expected = {
            new ExpressionStatement(DEFAULT_POS, a),
            new ExpressionStatement(DEFAULT_POS, new AddExpression(DEFAULT_POS, foo, foo)),
            new ExpressionStatement(DEFAULT_POS, new MethodInvocationOnObject(DEFAULT_POS, foo, bar.getName(), new Arguments(DEFAULT_POS))),
            new ExpressionStatement(DEFAULT_POS, new AssignmentExpression(DEFAULT_POS, a, new AddExpression(DEFAULT_POS,
                    new ArrayAccess(DEFAULT_POS, b, i),
                    new FieldAccess(DEFAULT_POS, c, d.getName())
            )))
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
        Expression cond = new BooleanValue(DEFAULT_POS, true);
        Statement empty = new EmptyStatement(DEFAULT_POS);
        ASTNode[] ifs_expected = {
            new IfStatement(DEFAULT_POS, cond, empty, null),
            new IfStatement(DEFAULT_POS, cond, empty, empty),
            new IfStatement(DEFAULT_POS, cond, new IfStatement(DEFAULT_POS, cond, empty, null), null),
            new IfStatement(DEFAULT_POS, cond, new IfStatement(DEFAULT_POS, cond, empty, empty), null),
        };
        testLexstringEqualASTNode(ifs, ifs_expected);
    }

    @Test
    @DisplayName("empty statement")
    void testEmptyStatement() {
        String[] empty = {";"};
        ASTNode[] empty_expected = {new EmptyStatement(DEFAULT_POS)};
        testLexstringEqualASTNode(empty, empty_expected);
    }
}
