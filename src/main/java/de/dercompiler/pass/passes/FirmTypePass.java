package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.transformation.FirmTypeFactory;
import firm.Entity;
import firm.Type;

/**
 * Sets the firm type(s) of every class, field, method and local variable statement
 * and creates entities for fields and local variable statements
 */
public class FirmTypePass implements ClassPass, MethodPass, StatementPass {
    private GlobalScope globalScope;
    private final FirmTypeFactory factory = FirmTypeFactory.getInstance();;
    
    @Override
    public void doInitialization(Program program) {
        globalScope = program.getGlobalScope();
    }
    
    @Override
    public void doFinalization(Program program) {}

    /**
     * For each class, this pass creates the firm type representing the class and sets it on the class.
     * Then the pass sets the corresponding firm types for all fields and creates entities for them.
     * The pass then adds the entities to the class.
     * @param classDeclaration The Class-Definition to run the Pass on.
     */
    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        // Get the definition and set the firm type
        ClassType def = globalScope.getClass(classDeclaration.getIdentifier());
        // We need to check that the firm type has not been set by a usage earlier
        if (def.getFirmType() == null) {
            // For class declarations, we always have to create a new firm type
            firm.ClassType firmType = factory.createFirmClassType(def);
            def.setFirmType(firmType);
            
            // Set the firm types for all fields and create entities for them
            for (ClassMember member : classDeclaration.getMembers()) {
                if (member instanceof Field f) {
                    if (f.getFirmType() == null) {
                        f.setFirmType(factory.getOrCreateFirmVariableType(f.getRefType()));
                    }
                    // Add the field entity to the parent class
                    firm.Entity entity = new Entity(def.getFirmType(), f.getMangledIdentifier(), f.getFirmType());
                    def.getFieldEntities().add(entity);
                }
            }

            def.getFirmType().layoutFields();
            def.getFirmType().finishLayout();
        }
        
        return false;
    }

    /**
     * For each method, this pass
     * 1. Checks whether the parameters already have a firm type set and creates new ones, if not
     * 2. Creates a firm type for the return type and the method itself
     * 3. Sets the firm type of the method
     * 4. Creates an entity for the method and adds that entity to the surrounding class
     * @param method The Method to run the Pass on.
     */
    @Override
    public boolean runOnMethod(Method method) {
        ClassDeclaration parentClass = method.getSurroundingClass();
        ClassType parentType = globalScope.getClass(parentClass.getIdentifier());

        // Get the definition and set the firm type
        MethodDefinition def = globalScope.getMethod(method.getSurroundingClass().getIdentifier(),
                method.getIdentifier());
        // We need to collect the firm types of the parameters and the return type
        firm.Type returnType = factory.getOrCreateFirmVariableType(def.getType().getReturnType());
        //0 this pointer
        int baseIdx = 0;
        int argCount = method.getParameters().size();
        firm.Type[] parameterTypes;
        if (method.isStatic()) {
            // main shall have no parameters
            argCount = 0;
            parameterTypes = new Type[0];
        } else {
            baseIdx++;
            argCount++;
            parameterTypes = new Type[argCount];
            parameterTypes[0] = parentType.getFirmType();
        }

        for (int i = baseIdx; i < argCount; i++) {
            Parameter p = method.getParameters().get(i - baseIdx);
            // If the parameter does not have a firm type set already, create one
            if (p.getFirmType() == null) {
                // If a parameter is of a class type that we have not run on yet, we do that now to finalize it
                if (p.getRefType() instanceof ClassType parameterClassType) {
                    if (parameterClassType.getFirmType() == null) {
                        runOnClass(parameterClassType.getDecl());
                    }
                }
                
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

        // Add the method entity to the parent class
        Entity entity = new Entity(parentType.getFirmType(), method.getMangledIdentifier(), def.getFirmType());
        parentType.getMethodEntities().add(entity);

        return false;
    }

    /**
     * For each LocalVariableDeclarationStatement, this pass creates a new firm type and sets it on the statement
     * @param statement The Statement to run the Pass on.
     */
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
