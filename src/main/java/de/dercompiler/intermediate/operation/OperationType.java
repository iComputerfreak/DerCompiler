package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.selection.Datatype;

public enum OperationType {
    //Erst die binary Operations
    MOVSLQ("MOVSLQ", false),
    ADD("ADD", true),
    AND("AND", true),
    CMP("CMP"),
    DIV("IDIV", true),
    MOV("MOV", true),
    OR("OR", true),
    ROL("ROL", true),
    ROR("ROR", true),
    SHL("SHL", true),
    SAR("SAR", true),
    SAL("SAL", true),
    SHR("SHR", true),
    SHRS("SHRS"), // arithmetic ("signed") right shift
    SUB("SUB", true),
    XCHG("XCHG", true),
    XOR("XOR", true),
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
    NEG("NEG", true),
    MOD("MOD", true),
    NOT(""),
    POP("POP"),
    PUSH("PUSH"),
    INC("INC", true),
    //Dann die constant Operations
    NOP("NOP"),
    RET("RET"),
    CWTL("CWTL"),
    CLTQ("CLTQ"),
    LBL("LBL"),
    ;

    private String syntax;
    private boolean appendDatatype = false;

    OperationType(String syntax){
        this.syntax = syntax;
    }

    OperationType(String syntax, boolean appendDatatype){
        this.syntax = syntax;
        this.appendDatatype = appendDatatype;
    }

    public String getSyntax(){
        return syntax;
    }

    public String getAtntSyntax(Datatype datatype){
        if (appendDatatype){
            return syntax +  datatype.toString();
        }
        return syntax;
    }
}
