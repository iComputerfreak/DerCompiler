package de.dercompiler.semantic.type;

import de.dercompiler.ast.Program;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.IntType;
import de.dercompiler.semantic.GlobalScope;

import java.util.Objects;

public class TypeFactory {

    private static TypeFactory singleton;
    private GlobalScope globalScope;

    private TypeFactory() {

    }

    public static TypeFactory getInstance() {
        return Objects.isNull(singleton) ? (singleton = new TypeFactory()) : singleton;
    }

    public void initialize(Program program) {
        this.globalScope = program.getGlobalScope();
    }

    public Type create(de.dercompiler.ast.type.Type type) {
        BasicType basicType = type.getBasicType();
        if (type.getArrayDimension() > 0) {
            return createArrayType(basicType, type.getArrayDimension());
        }

        if (basicType instanceof de.dercompiler.ast.type.BooleanType) return new BooleanType();
        else if (basicType instanceof IntType) return new IntegerType();
        else if (basicType instanceof de.dercompiler.ast.type.VoidType) return new VoidType();
        else if (basicType instanceof CustomType customType) return create(customType);
        else {
            // TODO: Type is ErrorType, how do we react?
            return null;
        }
    }

    public ArrayType createArrayType(BasicType basicType, int dimension) {
        if (dimension > 1) {
            return new ArrayType(createArrayType(basicType, dimension - 1));
        } else {
            return new ArrayType(create(new de.dercompiler.ast.type.Type(null, basicType, 0)));
        }
    }

    public Type create(CustomType customType) {
        return globalScope.getClass(customType.getIdentifier());
    }
}
