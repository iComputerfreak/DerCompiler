package de.dercompiler.linker;

public sealed interface Compiler permits Gcc, Clang, MSVC_CL {

    boolean checkCompiler();

    void compile(CompilerCall call);

}
