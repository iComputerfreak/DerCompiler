package de.dercompiler.ast;

import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.StringTable;

/**
 * Represents a class member, such as a field, method or main method
 */
public abstract sealed class ClassMember extends ASTNode permits Field, Method, MainMethod, ErrorClassMember {

    /**
     * Creates a new ClassMember
     * @param position The source code position
     */
    public ClassMember(SourcePosition position) {
        super(position);
    }

    /**
     * Represents a comparator to compare two {@link ClassMember}s by their identifier and type
     */
    public static class Comparator implements java.util.Comparator<ClassMember> {

            @Override
            public  int compare(ClassMember a, ClassMember b) {
                if (a instanceof Method ma) {
                    if (b instanceof Field) return -1;
                    else if (b instanceof Method mb)
                        return ma.getIdentifier().compareTo(mb.getIdentifier());
                    else if (b instanceof MainMethod mmb) {
                        return ma.getIdentifier().compareTo(mmb.getIdentifier());
                    }
                } else if (a instanceof MainMethod mma) {
                    if (b instanceof Field) return -1;
                    else if (b instanceof Method mb)
                        return mma.getIdentifier().compareTo(mb.getIdentifier());
                } else if (a instanceof Field fa) {
                    if (b instanceof Method || b instanceof MainMethod) return 1;
                    else if (b instanceof Field fb) {
                        return fa.getIdentifier().compareTo(fb.getIdentifier());
                    }
                }
                return 1;
            }

    }
}
