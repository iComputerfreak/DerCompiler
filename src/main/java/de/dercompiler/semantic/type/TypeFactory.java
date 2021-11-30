package de.dercompiler.semantic.type;

import de.dercompiler.ast.Program;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.IntType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.Pass;
import de.dercompiler.pass.PassErrorIds;
import de.dercompiler.pass.PassManager;
import de.dercompiler.pass.PassManagerBuilder;
import de.dercompiler.pass.passes.TypeAnalysisPass;
import de.dercompiler.semantic.GlobalScope;

import java.util.Objects;

public class TypeFactory {

    private static TypeFactory singleton;
    private GlobalScope globalScope;
    private TypeAnalysisPass pass;
    private boolean createDummies;

    private TypeFactory() {
        this.createDummies = true;
    }

    public static TypeFactory getInstance() {
        return Objects.isNull(singleton) ? (singleton = new TypeFactory()) : singleton;
    }

    public void initialize(Program program, TypeAnalysisPass pass) {
        this.globalScope = program.getGlobalScope();
        this.pass = pass;
    }

    public Type create(de.dercompiler.ast.type.Type type) {
        BasicType basicType = type.getBasicType();
        if (type.getArrayDimension() > 0) {
            return createArrayType(basicType, type.getArrayDimension());
        }

        return create(basicType);
    }

    private Type create(BasicType basicType) {
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
            Type elementType = create(basicType);
            if (elementType instanceof InternalClass) {
                System.err.println(pass.getPassManager().getLexer().printSourceText(basicType.getSourcePosition()));
                pass.getLogger().printErrorAndExit(PassErrorIds.ILLEGAL_ARRAY_TYPE, "Illegal reference to internal construct '%s'".formatted(elementType));
                pass.getPassManager().quitOnError();
            }
            if (elementType instanceof VoidType) {
                System.err.println(pass.getPassManager().getLexer().printSourceText(basicType.getSourcePosition()));
                pass.getLogger().printErrorAndExit(PassErrorIds.ILLEGAL_ARRAY_TYPE, "Illegal array base type void".formatted(elementType));
                pass.getPassManager().quitOnError();
            }

            return new ArrayType(elementType);
        }
    }

    public ClassType create(CustomType customType) {
        if (globalScope.hasClass(customType.getIdentifier())) {
            return globalScope.getClass(customType.getIdentifier());
        }

        else if (createDummies) {
            DummyClassType dummy = new DummyClassType(customType.getIdentifier());
            globalScope.addClass(dummy);
            return dummy;
        }

        new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(PassErrorIds.UNKNOWN_TYPE, "Type '%s' is unknown".formatted(customType.getIdentifier()));
        throw new RuntimeException();
    }

    public void setCreateDummies(boolean createDummies) {
        this.createDummies = createDummies;
    }
}
