package de.dercompiler.linker;

import java.io.File;

public class ExternalToolchain {

    private static Compiler compiler = null;
    private static Linker linker = null;
    private static Assembler assembler = null;
    private static AssemblerStyle asmStyle = AssemblerStyle.ATAndT;

    public static Assembler getAssembler() { return assembler; }

    public static Compiler getCompiler() {
        return compiler;
    }

    public static Linker getLinker() {
        return linker;
    }

    public static void setAssembler(String tool) {
        //don't check if null, in case of null we search if we can find a linker
        assembler = CompilerLinkerFactory.checkIfAssemblerIsValid(tool);
    }

    public static void setLinker(String tool) {
        //don't check if null, in case of null we search if we can find a linker
        linker = CompilerLinkerFactory.checkIfLinkerIsValid(tool);
    }

    public static void setLinker(Linker link) {
        linker = link;
    }

    public static void setCompiler(String tool) {
        //don't check if null, in case of null we search if we can find a compiler
        compiler = CompilerLinkerFactory.checkIfCompilerIsValid(tool);
    }

    public static void setCompiler(Compiler comp) {
        compiler = comp;
    }

    public static boolean unsafeCompile(CompilerCall call) {
        if (compiler == null) return false;
        compiler.compile(call);
        return true;
    }

    public static boolean unsafeAssemble(AssemblerCall call) {
        if (assembler == null) return false;
        assembler.assemble(call);
        return true;
    }

    public static boolean unsafeLink(LinkerCall call) {
        if (linker == null) return false;
        linker.link(call);
        return true;
    }

    public static void generateOutFile(File assembly, File outputFile) {
        CompilerLinkerFinder.findCompilerAssemblerAndLinker();
        AssemblerCall call = new AssemblerCall(new String[]{ assembly.getAbsolutePath() }, outputFile.getAbsolutePath());
        assembler.assemble(call);
    }

    public static void compileRuntime(File runtime, File outputFile) {
        CompilerLinkerFinder.findCompilerAssemblerAndLinker();
        CompilerCall call = new CompilerCall(new String[]{ runtime.getAbsolutePath() }, outputFile.getAbsolutePath());
        compiler.compile(call);
    }

    public static void linkRuntime(File[] files, File target) {
        CompilerLinkerFinder.findCompilerAssemblerAndLinker();
        LinkerCall call = new LinkerCall(ToolchainUtil.filesToStrings(files), target.getAbsolutePath());
        linker.link(call);
    }

    public static void setAssemblerStyle(AssemblerStyle style) {
        asmStyle = style;
    }

    public static AssemblerStyle getAssemblerStyle() {
        return asmStyle;
    }
}
