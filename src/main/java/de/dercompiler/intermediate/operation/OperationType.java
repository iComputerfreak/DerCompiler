package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.selection.Datatype;

public enum OperationType {
    //Erst die binary Operations
    MOVSLQ("MOVSLQ"),
    ADD("ADD"),
    AND("AND"),
    CMP("CMP"),
    DIV("DIV"),
    MOV("MOV"),
    OR("OR"),
    ROL("ROL"),
    ROR("ROR"),
    SHL("SHL"),
    SAR("SAR"),
    SAL("SAL"),
    SHR("SHR"),
    SHRS("SHRS"), // arithmetic ("signed") right shift
    SUB("SUB"),
    XCHG("XCHG"),
    XOR("XOR"),
    LOAD(""),
    STORE(""),
    //Dann die unary Operations
    CALL("CALL"),
    DEC("DEC"),
    MUL("MUL"),
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
    NEG("NEG"),
    MOD("MOD"),
    NOT(""),
    POP("POP"),
    PUSH("PUSH"),
    INC("INC"),
    //Dann die constant Operations
    NOP("NOP"),
    RET("RET"),
    CWTL("CWTL"),
    CLTQ("CLTQ"),
    LBL("LBL"),
    ;

    private String syntax;

    OperationType(String syntax){
        this.syntax = syntax;
    }

    public String getSyntax(){
        return syntax;
    }

    public String getAtntSyntax(Datatype datatype){
        return syntax + datatype.toString();
    }
}
