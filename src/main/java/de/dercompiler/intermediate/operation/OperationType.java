package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.selection.Datatype;

public enum OperationType {
    // Ternary operations
    IMUL3("IMUL"),

    // Binary operations
    MOVSLQ("MOVSLQ", false),
    ADD("ADD", true),
    AND("AND", true),
    CMP("CMP", true),
    IDIV("IDIV", true),
    IMUL("IMUL", true),
    LEA("LEA", true),
    MOV("MOV", true),
    OR("OR", true),
    ROL("ROL", true),
    ROR("ROR", true),
    SHL("SHL", true),
    SAR("SAR", true),
    SAL("SAL", true),
    SHR("SHR", true),
    SUB("SUB", true),
    XCHG("XCHG", true),
    XOR("XOR", true),
    LOAD(""),
    STORE(""),

    // Unary operations
    CALL("CALL"),
    DIV("DIV"),
    DEC("DEC"),
    JA("JA"),
    JAE("JAE"),
    JB("JB"),
    JBE("JBE"),
    JE("JE"),
    JG("JG"),
    JGE("JGE"),
    JL("JL"),
    JLE("JLE"),
    JMP("JMP"),
    JNE("JNE"),
    NEG("NEG", true),
    MOD("MOD", true),
    LBL("LBL"),
    NOT(""),
    POP("POP"),
    PUSH("PUSH"),
    INC("INC", true),

    // Constant operations
    NOP("NOP"),
    RET("RET"),
    CWTL("CWTL"),
    CLTQ("CLTQ"),
    CQTO("CQTO"),
    ;

    private final String syntax;
    private final boolean appendDatatype;

    OperationType(String syntax){
        this.syntax = syntax.toLowerCase(); this.appendDatatype = false;
    }

    OperationType(String syntax, boolean appendDatatype){
        this.syntax = syntax.toLowerCase();
        this.appendDatatype = appendDatatype;
    }

    public String getSyntax(){
        return syntax;
    }

    public String getAtntSyntax(Datatype datatype){
        if (appendDatatype){
            return syntax +  datatype.toString().toLowerCase();
        }
        return syntax;
    }
}
