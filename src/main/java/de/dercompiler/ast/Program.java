package de.dercompiler.ast;

import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.util.Utils;

import java.util.List;

public final class Program implements ASTNode {

    private final SourcePosition position;
    private final List<ClassDeclaration> classes;

    public Program(SourcePosition position, List<ClassDeclaration> classes) {
        this.position = position;
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
    public SourcePosition getSourcePosition() {
        return position;
    }
}
