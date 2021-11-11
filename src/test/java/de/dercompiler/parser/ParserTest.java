package de.dercompiler.parser;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.IntegerValue;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.type.*;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.LexerTest;
import de.dercompiler.lexer.SourcePosition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static de.dercompiler.parser.ParserTestHelper.DEFAULT_POS;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    
    ParserTestHelper helper = new ParserTestHelper();
    
    static SourcePosition POS = DEFAULT_POS;

    static Type INT_TYPE = new Type(POS, new IntType(POS), 0);
    static Type VOID_TYPE = new Type(POS, new VoidType(POS), 0);
    static Type BOOLEAN_TYPE = new Type(POS, new BooleanType(POS), 0);

    @BeforeAll
    static void setup() {
        //OutputMessageHandler.setDebug();
    }

    @BeforeEach
    void beforeTests() {
        OutputMessageHandler.clearDebugEvents();
    }

    @Test
    void testTypes() {
        // BasicType
        assertSyntaxEquals(parser("int").parseBasicType(), new IntType(POS));
        assertSyntaxEquals(parser("int").parseBasicType(), new IntType(POS));
        assertSyntaxEquals(parser("void").parseBasicType(), new VoidType(POS));
        assertSyntaxEquals(parser("boolean").parseBasicType(), new BooleanType(POS));
        assertSyntaxEquals(parser("TestType").parseBasicType(), new CustomType(POS, "TestType"));

        // Type
        assertSyntaxEquals(parser("int").parseType(new AnchorSet()), INT_TYPE);
        assertSyntaxEquals(parser("void").parseType(new AnchorSet()), VOID_TYPE);
        assertSyntaxEquals(parser("boolean").parseType(new AnchorSet()), BOOLEAN_TYPE);
        assertSyntaxEquals(parser("TestType").parseType(new AnchorSet()), new Type(POS, new CustomType(POS, "TestType"), 0));

        assertSyntaxEquals(parser("int[]").parseType(new AnchorSet()), new Type(POS, new IntType(POS), 1));
        assertSyntaxEquals(parser("void[]").parseType(new AnchorSet()), new Type(POS, new VoidType(POS), 1));
        assertSyntaxEquals(parser("boolean[]").parseType(new AnchorSet()), new Type(POS, new BooleanType(POS), 1));
        assertSyntaxEquals(parser("TestType[]").parseType(new AnchorSet()), new Type(POS, new CustomType(POS, "TestType"), 1));

        assertSyntaxEquals(parser("int[][][][][]").parseType(new AnchorSet()), new Type(POS, new IntType(POS), 5));
        assertSyntaxEquals(parser("void[][][][][]").parseType(new AnchorSet()), new Type(POS, new VoidType(POS), 5));
        assertSyntaxEquals(parser("boolean[][][][][]").parseType(new AnchorSet()), new Type(POS, new BooleanType(POS), 5));
        assertSyntaxEquals(parser("TestType[][][][][]").parseType(new AnchorSet()), new Type(POS, new CustomType(POS, "TestType"), 5));
    }
    
    @Test
    void testClassMembers() {
        // Field
        assertSyntaxEquals(parser("public int Integer;").parseClassMember(new AnchorSet()), new Field(POS, INT_TYPE, "Integer"));
        assertSyntaxEquals(parser("public void[] VoidArray;").parseClassMember(new AnchorSet()), new Field(POS, new Type(POS, new VoidType(POS), 1), "VoidArray"));
        assertSyntaxEquals(parser("public MyType MyType;").parseClassMember(new AnchorSet()), new Field(POS, new Type(POS, new CustomType(POS, "MyType"), 0), "MyType"));

        // MainMethod
        assertSyntaxEquals(parser("public static void main(String[] args) throws Nothing {}").parseClassMember(new AnchorSet()),
                new MainMethod(POS, "main", new Type(POS, new CustomType(POS, "String"), 1), "args", new MethodRest(POS, "Nothing"), new BasicBlock(POS)));

        // Method & MethodRest & Parameter
        assertSyntaxEquals(parser("public void[] function(String[] args) throws SomeError {}").parseClassMember(new AnchorSet()),
                new Method(POS, new Type(POS, new VoidType(POS), 1), "function",
                        List.of(new Parameter(POS, new Type(POS, new CustomType(POS, "String"), 1), "args")),
                        new MethodRest(POS, "SomeError"), new BasicBlock(POS)));

        assertSyntaxEquals(parser("public int foo() {}").parseClassMember(new AnchorSet()),
                new Method(POS, INT_TYPE, "foo",
                        new ArrayList<>(), null, new BasicBlock(POS)));

        assertSyntaxEquals(parser("public boolean foo(int a, void b, boolean c) {}").parseClassMember(new AnchorSet()),
                new Method(POS, BOOLEAN_TYPE, "foo",
                        List.of(
                                new Parameter(POS, INT_TYPE, "a"),
                                new Parameter(POS, VOID_TYPE, "b"),
                                new Parameter(POS, BOOLEAN_TYPE, "c")
                        ), null, new BasicBlock(POS)));
    }
    
    @Test
    void testClassDeclarations() {
        // ClassDeclaration
        assertSyntaxEquals(parser("class Foo {}").parseClassDeclaration(new AnchorSet()),
                new ClassDeclaration(POS, "Foo", new ArrayList<>()));

        assertSyntaxEquals(parser("class Foo { public int a; }").parseClassDeclaration(new AnchorSet()),
                new ClassDeclaration(POS, "Foo", List.of(new Field(POS, INT_TYPE, "a"))));

        assertSyntaxEquals(parser("class _Foo123 { public void[] a; public String foo(boolean b) {} public static void main(String[] args) throws NullPointerException {} }").parseClassDeclaration(new AnchorSet()),
                new ClassDeclaration(POS, "_Foo123", List.of(
                        new Field(POS, new Type(POS, new VoidType(POS), 1), "a"),
                        new Method(POS, new Type(POS, new CustomType(POS, "String"), 0), "foo", List.of(
                                new Parameter(POS, BOOLEAN_TYPE, "b")
                        ), null, new BasicBlock(POS)),
                        new MainMethod(POS, "main", new Type(POS, new CustomType(POS, "String"), 1), "args", new MethodRest(POS, "NullPointerException"), new BasicBlock(POS))
                )));
    }
    
    @Test
    void testProgram() {
        // Program
        assertSyntaxEquals(parser("class _Foo123 { public void[] a; public String foo(boolean b) {} public static void main(String[] args) throws NullPointerException {} } class _Foo123 { public void[] a; public String foo(boolean b) {} public static void main(String[] args) throws NullPointerException {} }").parseProgram(),
                new Program(POS, List.of(
                        new ClassDeclaration(POS, "_Foo123", List.of(
                                new Field(POS, new Type(POS, new VoidType(POS), 1), "a"),
                                new Method(POS, new Type(POS, new CustomType(POS, "String"), 0), "foo", List.of(
                                        new Parameter(POS, BOOLEAN_TYPE, "b")
                                ), null, new BasicBlock(POS)),
                                new MainMethod(POS, "main", new Type(POS, new CustomType(POS, "String"), 1), "args", new MethodRest(POS, "NullPointerException"), new BasicBlock(POS))
                        )),

                        new ClassDeclaration(POS, "_Foo123", List.of(
                                new Field(POS, new Type(POS, new VoidType(POS), 1), "a"),
                                new Method(POS, new Type(POS, new CustomType(POS, "String"), 0), "foo", List.of(
                                        new Parameter(POS, BOOLEAN_TYPE, "b")
                                ), null, new BasicBlock(POS)),
                                new MainMethod(POS, "main", new Type(POS, new CustomType(POS, "String"), 1), "args", new MethodRest(POS, "NullPointerException"), new BasicBlock(POS))
                        ))
                )));
    }
    
    @Test
    void testBlockContents() {
        String sampleStatements = "int a = 0;";
        List<Statement> sampleStatementsResult = List.of(new LocalVariableDeclarationStatement(POS, INT_TYPE, "a", new IntegerValue(POS, "0")));

        assertSyntaxEquals(parser("public int foo() { " + sampleStatements + " }").parseClassMember(new AnchorSet()),
                new Method(POS, INT_TYPE, "foo", new ArrayList<>(), null, new BasicBlock(POS, sampleStatementsResult)));

        assertSyntaxEquals(parser("public static void foo(int args) { " + sampleStatements + " }").parseClassMember(new AnchorSet()),
                new MainMethod(POS, "foo", INT_TYPE, "args", null, new BasicBlock(POS, sampleStatementsResult)));
    }

    @Test
    void testCases() {
        // Test the output for all files
        for (File file : getResourceFolderFiles("parser")) {
            String filename = file.getName();
            // Skip output files for now (and any other files that are not test cases)

            System.out.println("Testing file " + filename);
            Lexer l = Lexer.forFile(new File(file.getPath()));
            Parser p = new Parser(l);

            OutputMessageHandler.setDebug();

            // Tests that should succeed
            if (filename.endsWith(".valid.mj")) {
                assertDoesNotThrow(p::parseProgram);
                assertTrue(OutputMessageHandler.getEvents().isEmpty());
            } else if (filename.endsWith(".invalid.mj")) {
                // Make sure that the test really fails
                boolean error = false;
                try {
                    p.parseProgram();
                    assertFalse(OutputMessageHandler.getEvents().isEmpty());
                    error = true;
                } catch (Exception e) {
                    error = true;
                }
                assertTrue(error);
                OutputMessageHandler.clearDebugEvents();

            }
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

    private static Parser parser(String input) {
        return new Parser(Lexer.forString(input));
    }
    
    static void assertSyntaxEquals(ASTNode actual, ASTNode expected) {
        assertTrue(expected.syntaxEquals(actual), "Syntax not matching. Expected '" + expected + "', but got '" + actual.toString() + "'.");
    }
    
}
