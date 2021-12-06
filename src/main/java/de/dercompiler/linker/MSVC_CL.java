package de.dercompiler.linker;

public final class MSVC_CL implements Compiler, Linker {

    @Override
    public boolean checkCompiler() {
        return false;
    }

    @Override
    public void compile(CompilerCall call) {

    }

    @Override
    public boolean checkLinker() {
        return false;
    }

    @Override
    public void link(LinkerCall call) {

    }
}
