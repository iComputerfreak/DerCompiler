package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.util.Utils;

import java.util.List;

public final class Program extends ASTNode {

    private final List<ClassDeclaration> classes;

    public Program(SourcePosition position, List<ClassDeclaration> classes) {
        super(position);
        this.classes = classes;
    }

    public List<ClassDeclaration> getClasses() {
        return classes;
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
        astNodeVisitor.visitProgram(this);
    }
}
