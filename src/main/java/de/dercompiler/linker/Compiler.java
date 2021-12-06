package de.dercompiler.linker;

public sealed interface Compiler permits Gcc, Clang, MSVC  {

    boolean checkCompiler();


}
