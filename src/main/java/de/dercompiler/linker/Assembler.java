package de.dercompiler.linker;

public sealed interface Assembler permits NASM, Gcc {

    boolean checkAssembler();

    void assemble(AssemblerCall call);
}
