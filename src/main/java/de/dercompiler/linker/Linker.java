package de.dercompiler.linker;

public sealed interface Linker permits ClangLLD, GccLD, MSVC_CL {

    boolean checkLinker();

    void link(LinkerCall call);


}
