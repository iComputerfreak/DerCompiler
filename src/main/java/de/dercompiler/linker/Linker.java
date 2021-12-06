package de.dercompiler.linker;

public sealed interface Linker permits GccLD, MSVCLink, ClangLLD {

    boolean checkLinker();



}
