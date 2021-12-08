package de.dercompiler.linker;

public final class ClangLLD implements Linker {

    private static String lld_path;

    public ClangLLD(String lld) {
        lld_path = lld;
    }

    @Override
    public boolean checkLinker() {
        return false;
    }

    @Override
    public void link(LinkerCall call) {

    }
}
