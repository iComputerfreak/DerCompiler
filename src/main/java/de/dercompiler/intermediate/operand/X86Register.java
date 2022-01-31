package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;

public enum X86Register implements Register {
    RAX("rax", "eax", "ax", "al"),
    RCX("rcx", "ecx", "cx", "al"),
    RDX("rdx", "edx", "dx", "dl"),
    RBX("rbx", "ebx", "bx", "bl"),
    RSI("rsi", "esi", "si", "sil"),
    RDI("rdi", "edi", "di", "dil"),
    RSP("rsp", "esp", "sp", "spl"),
    RBP("rbp", "ebp", "bp", "bpl"),
    R8("r8"),
    R9("r9"),
    R10("r10"),
    R11("r11"),
    R12("r12"),
    R13("r13"),
    R14("r14"),
    R15("r15");

    private final String id;
    private final String idd;
    private final String idw;
    private final String idb;

    X86Register(String id) {
        this.id = id;
        this.idd = id + "d";
        this.idw = id + "w";
        this.idb = id + "b";
    }

    X86Register(String id8, String id4, String id2, String id1) {
        this.id = id8;
        this.idd = id4;
        this.idw = id2;
        this.idb = id1;
    }

    public Operand offset(int offset) {
        return new Address(offset, this);
    }


    @Override
    public String getIdentifier() {
        return "%" + id;
    }


    @Override
    public String getIdentifier(Datatype datatype) {
        return "%" + switch (datatype) {
            case BYTE -> idb;
            case WORD -> idw;
            case DWORD -> idd;
            case QWORD, OTHER, NODATA -> id;
        };
    }
}
