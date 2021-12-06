package de.dercompiler.linker;

public final class Gcc implements Compiler {

    @Override
    public boolean checkCompiler() {
        return false;
    }

    @Override
    public void compile(CompilerCall call) {

    }
}
