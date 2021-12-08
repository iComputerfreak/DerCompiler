package de.dercompiler.linker;

public sealed interface Assambler permits NASM, Gcc {

    boolean checkAssembler();

    void assemble(AssamblerCall call);
}
