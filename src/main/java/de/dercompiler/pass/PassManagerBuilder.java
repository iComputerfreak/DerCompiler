package de.dercompiler.pass;

import de.dercompiler.pass.passes.*;

public class PassManagerBuilder {

    public static void buildSemanticsPipeline(PassManager manager) {
        // name-analysis passes
        manager.addPass(new InterClassAnalysisCheckPass());
        manager.addPass(new MethodDeclarationPass());
        manager.addPass(new EnterScopePass());
        manager.addPass(new VariableAnalysisCheckPass());
        manager.addPass(new LeaveScopePass());

        // type-analysis passes
        manager.addPass(new TypeAnalysisPass());

        // specification-related passes
        manager.addPass(new SpecificationConformityPass());
    }
}
