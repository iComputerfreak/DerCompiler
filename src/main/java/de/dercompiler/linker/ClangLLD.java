package de.dercompiler.linker;

public final class ClangLLD implements Linker {

    @Override
    public boolean checkLinker() {
        return false;
    }

    @Override
    public void link(LinkerCall call) {

    }
}
