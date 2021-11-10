package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.util.Utils;

import java.util.List;

public final class Method extends ClassMember {

    private final Type type;
    private final String identifier;
    private final List<Parameter> parameters;
    private final MethodRest methodRest;
    private final BasicBlock block;
    private ClassDeclaration surrounding;
    
    // INFO: methodRest may be null
    public Method(SourcePosition position, Type type, String identifier, List<Parameter> parameters, MethodRest methodRest, BasicBlock block) {
        super(position);
        this.type = type;
        this.identifier = identifier;
        this.parameters = parameters;
        this.methodRest = methodRest;
        this.block = block;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public MethodRest getMethodRest() {
        return methodRest;
    }

    public BasicBlock getBlock() {
        return block;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof Method otherMethod) {
            // If this rest is null, but the other is not, return false
            if (this.methodRest == null && otherMethod.methodRest != null) {
                return false;
            }
            // If this rest is not null, both rests must have equal syntax
            if (this.methodRest != null && !this.methodRest.syntaxEquals(otherMethod.methodRest)) {
                return false;
            }
            return this.type.syntaxEquals(otherMethod.type)
                    && this.identifier.equals(otherMethod.identifier)
                    && Utils.syntaxEquals(this.parameters, otherMethod.parameters)
                    && this.block.syntaxEquals(otherMethod.block);
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitMethod(this);
    }

    public void setSurroundingClass(ClassDeclaration declaration) {
        this.surrounding = declaration;
    }

    public ClassDeclaration getSurroundingClass() {
        return surrounding;
    }
}
