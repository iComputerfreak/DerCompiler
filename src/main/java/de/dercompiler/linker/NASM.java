package de.dercompiler.linker;

public final class NASM implements Assembler {

    private String nasm_path;

    public NASM(String nasm) {
        nasm_path = nasm;
    }

    @Override
    public boolean checkAssembler() {
        return false;
    }

    @Override
    public void assemble(AssemblerCall call) {

    }
}
