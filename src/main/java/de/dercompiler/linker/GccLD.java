package de.dercompiler.linker;

public final class GccLD implements Linker {


    @Override
    public boolean checkLinker() {
        return false;
    }

    @Override
    public void link(LinkerCall call) {

    }
}
