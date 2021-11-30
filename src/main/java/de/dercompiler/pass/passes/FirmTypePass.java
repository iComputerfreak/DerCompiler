package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.transformation.FirmTypeFactory;

/**
 * Sets the firm type(s) of every class, field, method and local variable
 */
public class FirmTypePass implements ClassPass, MethodPass, StatementPass {
    // TODO: Add to PassManager
    private GlobalScope globalScope;
    private final FirmTypeFactory factory = FirmTypeFactory.getInstance();;
    
    @Override
    public void doInitialization(Program program) {
        globalScope = program.getGlobalScope();
    }
    
    @Override
    public void doFinalization(Program program) {}
    
    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        // Get the definition and set the firm type
        ClassType def = globalScope.getClass(classDeclaration.getIdentifier());
        // We need to check that the firm type has not been set by a usage earlier
        if (def.getFirmType() == null) {
            // For class declarations, we always have to create a new firm type
            firm.ClassType firmType = factory.createFirmClassType(def);
            def.setFirmType(firmType);
        }
        
        // Set the firm types for all fields
        for (ClassMember member : classDeclaration.getMembers()) {
            if (member instanceof Field f) {
                if (f.getFirmType() == null) {
                    f.setFirmType(factory.getOrCreateFirmVariableType(f.getRefType()));
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean runOnMethod(Method method) {
        // Get the definition and set the firm type
        MethodDefinition def = globalScope.getMethod(method.getSurroundingClass().getIdentifier(),
                method.getIdentifier());
        // We need to collect the firm types of the parameters and the return type
        firm.Type returnType = factory.getOrCreateFirmVariableType(def.getType().getReturnType());
        firm.Type[] parameterTypes = new firm.Type[method.getParameters().size()];
        for (int i = 0; i < parameterTypes.length; i++) {
            Parameter p = method.getParameters().get(i);
            // If the parameter does not have a firm type set already, create one
            if (p.getFirmType() == null) {
                p.setFirmType(factory.getOrCreateFirmVariableType(p.getRefType()));
            }
            // Save the firm type to the array
            parameterTypes[i] = p.getFirmType();
        }
        
        // The method firm type should never be set earlier, but just to be sure
        if (def.getFirmType() == null) {
            // For method definitions, we always have to create a new firm type
            firm.MethodType firmType = factory.createFirmMethodType(parameterTypes, returnType);
            def.setFirmType(firmType);
        }
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        if (statement instanceof LocalVariableDeclarationStatement s) {
            if (s.getFirmType() == null) {
                firm.Type firmType = factory.getOrCreateFirmVariableType(s.getRefType());
                s.setFirmType(firmType);
            }
        }
        return false;
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(VariableAnalysisCheckPass.class);
        usage.setDependency(DependencyType.RUN_DIRECTLY_AFTER);
        return usage;
    }
    
    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return null;
    }
    
    private static long id = 0;
    private PassManager manager = null;
    
    @Override
    public void registerPassManager(PassManager manager) {
        this.manager = manager;
    }
    
    @Override
    public PassManager getPassManager() {
        return manager;
    }
    
    @Override
    public long registerID(long rid) {
        if (id != 0) return id;
        id = rid;
        return id;
    }
    
    @Override
    public long getID() {
        return id;
    }
    
    @Override
    public AnalysisDirection getAnalysisDirection() {
        return AnalysisDirection.TOP_DOWN;
    }
}
