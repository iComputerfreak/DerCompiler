package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.StringTable;
import de.dercompiler.semantic.SymbolTable;
import de.dercompiler.util.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * Represents a program in a MiniJava program. Example:
 * <pre>
 *     {@code
 *     class Foo {
 *         public int a;
 *         public static void foo(String[] args) throws NullPointerException {}
 *         public void bar() {
 *             this.a = 0;
 *         }
 *     }
 *     
 *     class Bar {}
 *     }
 * </pre>
 */
public final class Program extends ASTNode {

    private final List<ClassDeclaration> classes;
    private final ProgramNameSpace nameSpace;
    private boolean isIndexed;
    // TODO: SymbolTable field, getter and init in constructor
    private final SymbolTable symbolTable;

    public HashMap<String, ClassDeclaration> getClassMap() {
        return classMap;
    }

    private final HashMap<String, ClassDeclaration> classMap;

    /**
     * Creates a new Program
     * @param position The source code position
     * @param classes The list of classes in this program
     */
    public Program(SourcePosition position, List<ClassDeclaration> classes) {
        super(position);
        this.classes = classes;
        this.symbolTable = new SymbolTable();
        isIndexed = false;

        classMap = new HashMap<String, ClassDeclaration>();
        this.nameSpace = this.new ProgramNameSpace();
    }

    /**
     * Returns all classes in this program
     */
    public List<ClassDeclaration> getClasses() {
        return classes;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof Program otherProgram) {
            return Utils.syntaxEquals(this.classes, otherProgram.classes);
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        try {
            this.classes.sort(new ClassDeclaration.Comparator()::compare);
        } catch (UnsupportedOperationException e) {
            new OutputMessageHandler(MessageOrigin.AST).internalError("Tried to sort immutable list of ClassDeclarations.");
        }
        astNodeVisitor.visitProgram(this);
    }

    public boolean isIndexed() {
        return isIndexed;
    }

    public void indexed() {
        isIndexed = true;
    }

    public ProgramNameSpace getNameSpace() {
        return nameSpace;
    }

    public class ProgramNameSpace {

        public Method getMethod(String className, String methodName) {
            return getClass(className).getMethodMap().get(methodName);
        }

        public Field getField(String className, String fieldName) {
            return getClass(className).getFieldMap().get(fieldName);
        }

        public ClassDeclaration getClass(String className) {
            return Program.this.getClassMap().get(className);
        }
    }
}
