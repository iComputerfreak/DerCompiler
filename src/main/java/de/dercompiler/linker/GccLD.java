package de.dercompiler.linker;

public final class GccLD implements Linker {

    private String ld_path;

    public GccLD(String ld) {
        ld_path = ld;
    }

    @Override
    public boolean checkLinker() {
        return false;
    }

    @Override
    public void link(LinkerCall call) {

    }
}
