package de.dercompiler.linker;

import java.io.File;

public class ExternalToolchain {

    private static Compiler compiler = null;
    private static Linker linker = null;
    private static AssemblerStyle asmStyle = AssemblerStyle.ATAndT;

    public static Compiler getCompiler() {
        return compiler;
    }

    public static Linker getLinker() {
        return linker;
    }

    public static void setLinker(File file) {
        //don't check if null, in case of null we search if we can find a linker
        linker = CompilerLinkerFactory.checkIfLinkerIsValid(file);
    }

    public static void setLinker(Linker link) {
        linker = link;
    }

    public static void setCompiler(File file) {
        //don't check if null, in case of null we search if we can find a compiler
        compiler = CompilerLinkerFactory.checkIfCompilerISValid(file);
    }

    public static void setCompiler(Compiler comp) {
        compiler = comp;
    }

    public static void generateOutFile(File file) {
        CompilerLinkerFinder.findCompilerAssemblerAndLinker();

    }

    public static void compileRuntime(RuntimeFile file) {
        CompilerLinkerFinder.findCompilerAssemblerAndLinker();

    }

    public static void linkRuntime() {
        CompilerLinkerFinder.findCompilerAssemblerAndLinker();

    }

    public static void setAssemblerStyle(AssemblerStyle style) {
        asmStyle = style;
    }

    public static AssemblerStyle getAssemblerStyle() {
        return asmStyle;
    }
}
