package de.dercompiler.pass;

import de.dercompiler.pass.passes.*;

public class PassManagerBuilder {

    public static void buildSemanticsPipeline(PassManager manager) {
        // name-analysis passes
        manager.addPass(new InterClassAnalysisCheckPass());
        manager.addPass(new MemberDeclarationPass());
        manager.addPass(new GlobalsPass());
        manager.addPass(new EnterScopePass());
        manager.addPass(new VariableAnalysisCheckPass());
        manager.addPass(new LeaveScopePass());
        manager.addPass(new NullTypeCheckPass());

        // type-analysis passes
        manager.addPass(new TypeAnalysisPass());

        // specification-related passes
        manager.addPass(new SpecificationConformityPass());
    }

    public static void buildTransformationPipeline(PassManager manager) {
        buildSemanticsPipeline(manager);
        manager.addPass(new FirmTypePass());
        manager.addPass(new CountVariablesPass());
        manager.addPass(new DeadCodeEliminationPass().init(CountVariablesPass.class, DependencyType.RUN_DIRECTLY_AFTER));


        FirmMethodGraphStartupPass fms = new FirmMethodGraphStartupPass();
        FirmMethodGraphFinalizationPass fmf = new FirmMethodGraphFinalizationPass();
        fms.setFinalization(fmf);

        manager.addPass(fms);
        manager.addPass(fmf);
    }
}
