package de.dercompiler.transformation;

import de.dercompiler.semantic.type.*;
import firm.Mode;
import firm.PrimitiveType;
import java.util.List;

public class TransformUtil {
    
    public static firm.Type firmTypeForSemanticType(Type type) {
        if (type instanceof IntegerType) {
            return new PrimitiveType(Mode.getIs());
        } else if (type instanceof BooleanType) {
            // For booleans, we use the byte type (signed or unsigned should not matter)
            return new PrimitiveType(Mode.getBs());
        } else if (type instanceof VoidType) {
            // TODO: How to represent void?
            return null;
        } else if (type instanceof ArrayType t) {
            return new firm.ArrayType(firmTypeForSemanticType(t.getElementType()), t.getDimension());
        } else if (type instanceof ClassType t) {
            return new firm.ClassType(t.getIdentifier());
        } else if (type instanceof MethodType t) {
            // Convert parameters to firm.Type
            List<Type> parameterTypes = t.getParameterTypes();
            firm.Type[] parameters = new firm.Type[parameterTypes.size()];
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = firmTypeForSemanticType(parameterTypes.get(i));
            }
            // Convert return type
            firm.Type returnType = firmTypeForSemanticType(t.getReturnType());
            
            return new firm.MethodType(parameters, new firm.Type[] {returnType});
        } else if (type instanceof NullType) {
            // TODO: How to represent null
            return null;
        } else {
            // If we reach this, we requested a firm type for a semantic type that we did not consider above
            throw new RuntimeException("Requested a firm type for a type that is not yet implemented.");
        }
    }
    
}
