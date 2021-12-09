package de.dercompiler.linker;

import de.dercompiler.transformation.TargetTriple;

public final class GccLD implements Linker {

    private String ld_path;

    private static final String output = "-o";

    public GccLD(String ld) {
        ld_path = ld;
    }

    @Override
    public boolean checkLinker() {
        if (!TargetTriple.isLinux()) return false; //only linux support
        String test = ToolchainUtil.prepareTestCompile();

        String object = ToolchainUtil.appendObjectFileExtension(test);
        String exe = ToolchainUtil.appendExecutableExtension(test);
        CompilerCall call = new CompilerCall(new String[]{ ToolchainUtil.appendCFileExtension(test)}, object);
        if (!ExternalToolchain.unsafeCompile(call)) return false;

        Runner runner = new Runner(ld_path);
        runner.append(object);
        runner.append(output);
        runner.append(exe);
        if (!runner.run()) return false;

        Runner exeProgram = new Runner(exe);
        if (!exeProgram.run()) return false;
        return ToolchainUtil.checkTestCompile(exeProgram.getStdOut());
    }

    @Override
    public void link(LinkerCall call) {
        Runner runner = new Runner(ld_path);
        runner.append(call.objectFiles());
        runner.append(output);
        runner.append(call.targetName());
        runner.run();
    }
}
