package de.dercompiler.linker;

public final class NASM implements Assambler {

    private String nasm_path;

    public NASM(String nasm) {
        nasm_path = nasm;
    }

    @Override
    public boolean checkAssembler() {
        return false;
    }

    @Override
    public void assemble(AssamblerCall call) {

    }
}
